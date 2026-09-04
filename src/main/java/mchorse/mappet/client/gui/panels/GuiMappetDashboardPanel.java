package mchorse.mappet.client.gui.panels;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.utils.GuiStringFolderList;
import mchorse.mappet.client.gui.utils.GuiStringFolderSearchListElement;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentData;
import mchorse.mappet.network.common.content.PacketContentFolder;
import mchorse.mappet.network.common.content.PacketContentRequestData;
import mchorse.mappet.network.common.content.PacketContentRequestNames;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.modals.GuiConfirmModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDrawable;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_310;
import org.apache.commons.io.FilenameUtils;

public abstract class GuiMappetDashboardPanel<T extends AbstractData> extends GuiDashboardPanel<GuiMappetDashboard> {
   public static final IKey KEYS_CATEGORY = IKey.lang("mappet.gui.panels.keys.category");
   public GuiElement iconBar;
   public GuiIconElement toggleSidebar;
   public GuiElement sidebar;
   public GuiElement buttons;
   public GuiIconElement add;
   public GuiIconElement dupe;
   public GuiIconElement rename;
   public GuiIconElement remove;
   public GuiStringFolderSearchListElement names;
   public GuiStringFolderList namesList;
   public GuiElement editor;
   protected boolean update;
   protected T data;
   protected boolean allowed;
   protected boolean save;
   private String requestedDataId;

   public GuiMappetDashboardPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.buttons = new GuiElement(mc);
      this.sidebar = new GuiElement(mc);
      this.sidebar.flex().relative(this).x(1.0F).w(200).h(1.0F).anchorX(1.0F);
      this.iconBar = new GuiElement(mc);
      this.iconBar.flex().relative(this.sidebar).x(-20).w(20).h(1.0F).column(0).stretch();
      this.toggleSidebar = new GuiIconElement(mc, Icons.RIGHTLOAD, (element) -> this.toggleSidebar());
      this.iconBar.add(this.toggleSidebar);
      this.add = new GuiIconElement(mc, Icons.ADD, this::addNewData);
      this.add.context(() -> {
         GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);
         menu.action(Icons.ADD, IKey.lang("mappet.gui.panels.context.add_folder"), this::addFolder);
         return menu.shadow();
      });
      this.dupe = new GuiIconElement(mc, Icons.DUPE, this::dupeData);
      this.rename = new GuiIconElement(mc, Icons.EDIT, this::renameData);
      this.rename.context(() -> {
         if (this.namesList.getPath().isEmpty()) {
            return null;
         } else {
            GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);
            menu.action(Icons.EDIT, IKey.lang("mappet.gui.panels.context.rename_folder"), this::renameFolder);
            return menu.shadow();
         }
      });
      this.remove = new GuiIconElement(mc, Icons.REMOVE, this::removeData);
      this.remove.context(() -> {
         if (this.namesList.getPath().isEmpty()) {
            return null;
         } else {
            GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);
            menu.action(Icons.REMOVE, IKey.lang("mappet.gui.panels.context.remove_folder"), this::removeFolder);
            return menu.shadow();
         }
      });
      GuiDrawable drawable = new GuiDrawable((context) -> GuiDraw.drawStringWithShadow(this.font, class_1074.method_4662(this.getTitle(), new Object[0]), this.names.area.x, this.area.y + 10, 16777215));
      this.names = new GuiStringFolderSearchListElement(mc, (list) -> this.pickData((String)list.get(0)));
      this.namesList = (GuiStringFolderList)this.names.list;
      this.namesList.onFileDrop(this::moveDataToFolder);
      this.names.label(IKey.lang("mappet.gui.search"));
      this.names.flex().relative(this.sidebar).xy(10, 25).w(1.0F, -20).h(1.0F, -35);
      this.names.list.context(() -> {
         GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);
                     if (this.getType() == ContentType.SCRIPTS || this.getType() == ContentType.CLIENT_SCRIPTS) {

            menu.action(Icons.ADD, IKey.lang("mappet.gui.panels.context.add_script"), () -> this.addNewData(this.add));
            if (this.data != null && this.namesList.isContextFile()) {
               menu.action(Icons.REMOVE, IKey.lang("mappet.gui.panels.context.remove_script"), () -> this.removeData(this.remove));
            }
            menu.action(Icons.ADD, IKey.lang("mappet.gui.panels.context.add_folder"), this::addFolder);
            if (!this.namesList.getPath().isEmpty()) {
               menu.action(Icons.REMOVE, IKey.lang("mappet.gui.panels.context.remove_folder"), this::removeFolder);
            }
         }

         if (this.data != null) {
            menu.action(Icons.COPY, IKey.lang("mappet.gui.panels.context.copy"), this::copy);
         }

         try {
            class_2487 tag = class_2522.method_10718(GuiUtils.getClipboardString());
            if (tag.method_10558("_ContentType").equals(this.getType().getName())) {
               menu.action(Icons.PASTE, IKey.lang("mappet.gui.panels.context.paste"), () -> this.paste(tag));
            }
         } catch (Exception var4) {
         }

         if (mc.method_1542()) {
            menu.action(Icons.FOLDER, IKey.lang("mappet.gui.panels.context.open_folder"), () -> {
               if (this.getType().getManager() != null) {
                  String var10000 = this.getType().getManager().getFolder().getAbsolutePath();
                  String path = var10000 + "/" + this.namesList.getPath();
                  GuiUtils.openFolder(path);
               }
            });
         }

         return menu.actions.getList().isEmpty() ? null : menu.shadow();
      });
      this.sidebar.add(new IGuiElement[]{drawable, this.names, this.buttons});
      this.editor = new GuiElement(mc);
      this.editor.flex().relative(this).wTo(this.iconBar.area).h(1.0F);
      this.buttons.flex().relative(this.names).x(1.0F).y(-20).anchorX(1.0F).row(0).resize();
      this.buttons.add(new IGuiElement[]{this.add, this.dupe, this.rename, this.remove});
      this.markContainer();
      this.add(new IGuiElement[]{this.sidebar, this.iconBar, this.editor});
      this.keys().register(IKey.lang("mappet.gui.panels.keys.toggle_sidebar"), 49, () -> this.toggleSidebar.clickItself(GuiBase.getCurrent())).category(KEYS_CATEGORY);
   }

   private void copy() {
      class_2487 tag = (class_2487)this.data.serializeNBT();
      tag.method_10582("_ContentType", this.getType().getName());
      GuiUtils.setClipboardString(tag.toString());
   }

   private void paste(class_2487 tag) {
      T data = this.createClientData("", tag);
      if (data != null) {
         this.addNewData(this.add, data);
      } else {
         Mappet.LOGGER.error("Couldn't paste {}: content manager is not initialized", this.getType().getName());
      }
   }

   protected T createClientData(String id, class_2487 tag) {
      if (this.getType().getManager() == null) {
         return null;
      }

      return (T)this.getType().getManager().create(id, tag);
   }

   private void toggleSidebar() {
      this.sidebar.toggleVisible();
      this.toggleSidebar.both(this.sidebar.isVisible() ? Icons.RIGHTLOAD : Icons.LEFTLOAD);
      if (this.sidebar.isVisible()) {
         this.toggleWithSidebar();
         this.iconBar.flex().relative(this.sidebar).x(-20);
      } else {
         this.toggleFull();
         this.iconBar.flex().relative(this).x(1.0F, -20);
      }

      this.resize();
   }

   protected void toggleWithSidebar() {
      this.editor.flex().wTo(this.iconBar.area);
   }

   protected void toggleFull() {
      this.editor.flex().wTo(this.iconBar.area);
   }

   public abstract IContentType getType();

   public abstract String getTitle();

   public void pickData(String id) {
      this.save();
      this.requestedDataId = id;
      Dispatcher.sendToServer(new PacketContentRequestData(this.getType(), id));
   }

   public boolean acceptsData(String id) {
      if (this.requestedDataId == null) {
         return true;
      }

      if (!this.requestedDataId.equals(id)) {
         return false;
      }

      this.requestedDataId = null;
      return true;
   }

   protected void addNewData(GuiIconElement element) {
      this.addNewData((GuiIconElement)element, null);
   }

   protected void addNewData(GuiIconElement element, T data) {
      GuiModal.addFullModal(this.sidebar, () -> (new GuiPromptModal(this.mc, IKey.lang("mappet.gui.panels.modals.add"), (name) -> this.addNewData(this.namesList.getPath(name), data))).filename());
   }

   protected void addNewData(String name, T data) {
      if (!this.namesList.hasInHierarchy(name)) {
         this.save();
         Dispatcher.sendToServer(new PacketContentData(this.getType(), name, data == null ? new class_2487() : (class_2487)data.serializeNBT()));
         this.namesList.addFile(name);
         if (data == null) {
            data = this.createClientData(name, null);
            if (data == null) {
               Mappet.LOGGER.error("Couldn't create {}: content manager is not initialized", this.getType().getName());
               return;
            }

            this.fillDefaultData(data);
            if (this.getType().getManager() != null) {
               this.getType().getManager().create(data.getId(), (class_2487)data.serializeNBT());
            }
         } else {
            data.setId(name);
         }

         this.fill(data);
      }

   }

   protected void addFolder() {
      GuiModal.addFullModal(this.sidebar, () -> (new GuiPromptModal(this.mc, IKey.lang("mappet.gui.panels.modals.add_folder"), this::addFolder)).filename());
   }

   protected void addFolder(String name) {
      Dispatcher.sendToServer(new PacketContentFolder(this.getType(), name, this.namesList.getPath("")));
   }

   protected void renameFolder() {
      if (!this.namesList.getPath().isEmpty()) {
         String name = FilenameUtils.getBaseName(this.namesList.getPath());
         GuiModal.addFullModal(this.sidebar, () -> (new GuiPromptModal(this.mc, IKey.lang("mappet.gui.panels.modals.rename_folder"), this::renameFolder)).filename().setValue(name));
      }
   }

   protected void renameFolder(String name) {
      String path = this.namesList.getPath("");
      Dispatcher.sendToServer((new PacketContentFolder(this.getType(), "", path.substring(0, path.length() - 1))).rename(name));
      this.fill(null);
   }

   protected void removeFolder() {
      if (!this.namesList.getPath().isEmpty()) {
         GuiModal.addFullModal(this.sidebar, () -> new GuiConfirmModal(this.mc, IKey.lang("mappet.gui.panels.modals.remove_folder"), this::removeFolder));
      }
   }

   protected void removeFolder(Boolean isDelete) {
      if (isDelete) {
         String path = this.namesList.getPath("");
         Dispatcher.sendToServer((new PacketContentFolder(this.getType(), "", path.substring(0, path.length() - 1))).delete());
      }

   }

   protected void fillDefaultData(T data) {
   }

   protected void dupeData(GuiIconElement element) {
      if (this.data != null) {
         GuiModal.addFullModal(this.sidebar, () -> {
            GuiPromptModal promptModal = new GuiPromptModal(this.mc, IKey.lang("mappet.gui.panels.modals.dupe"), this::dupeData);
            return promptModal.setValue(this.data.getId()).filename();
         });
      }
   }

   protected void dupeData(String name) {
      if (!this.namesList.hasInHierarchy(name)) {
         this.save();
         Dispatcher.sendToServer(new PacketContentData(this.getType(), name, (class_2487)this.data.serializeNBT()));
         this.namesList.addFile(name);
         T data = this.createClientData(name, (class_2487)this.data.serializeNBT());
         if (data != null) {
            this.fill(data);
         } else {
            Mappet.LOGGER.error("Couldn't duplicate {}: content manager is not initialized", this.getType().getName());
         }
      }

   }

   protected void renameData(GuiIconElement element) {
      if (this.data != null) {
         GuiModal.addFullModal(this.sidebar, () -> {
            GuiPromptModal promptModal = new GuiPromptModal(this.mc, IKey.lang("mappet.gui.panels.modals.rename"), this::renameData);
            return promptModal.setValue(this.data.getId().substring(this.data.getId().lastIndexOf(47) + 1)).filename();
         });
      }
   }

   protected void renameData(String name) {
      if (!this.namesList.hasInHierarchy(name)) {
         String path = this.getDataPath();
         Dispatcher.sendToServer((new PacketContentData(this.getType(), this.data.getId(), (class_2487)this.data.serializeNBT())).rename(path + name));
         this.namesList.removeFile(this.data.getId());
         this.namesList.addFile(path + name);
         this.data.setId(path + name);
      }

   }

   protected String getDataPath() {
      String output = "";
      int index = this.data.getId().lastIndexOf(47);
      if (index != -1) {
         output = this.data.getId().substring(0, index + 1);
      }

      return output;
   }

   protected void moveDataToFolder(String source, String folder) {
      if (source == null || source.isEmpty() || folder == null) return;
      String fileName = FilenameUtils.getName(source);
      String target = folder.isEmpty() ? fileName : (folder.endsWith("/") ? folder + fileName : folder + "/" + fileName);
      if (source.equals(target) || this.namesList.hasInHierarchy(target)) return;
      Dispatcher.sendToServer(new PacketContentData(this.getType(), source).rename(target));
      this.namesList.keepFolder(FilenameUtils.getPathNoEndSeparator(source).replace('\\', '/'));
      this.namesList.removeFile(source);
      this.namesList.addFile(target);
      if (this.data != null && source.equals(this.data.getId())) {
         this.data.setId(target);
      }
   }

   protected void removeData(GuiIconElement element) {
      if (this.data != null) {
         GuiModal.addFullModal(this.sidebar, () -> new GuiConfirmModal(this.mc, IKey.lang("mappet.gui.panels.modals.remove"), this::removeData));
      }
   }

   protected void removeData(boolean confirm) {
      if (this.data != null && confirm) {
         Dispatcher.sendToServer(new PacketContentData(this.getType(), this.data.getId(), (class_2487)null));
         String removedId = this.data.getId();
         this.namesList.removeFile(removedId);
         this.onDataRemoved(removedId);
         this.fill(null);
      }

   }

   public final void fill(T data) {
      this.fill(data, true);
   }

   public void fill(T data, boolean allowed) {
      this.data = data;
      this.allowed = allowed;
      this.namesList.setCurrentFile(data == null ? null : data.getId());
      this.editor.setEnabled(allowed);
      this.remove.setEnabled(allowed);
      this.rename.setEnabled(allowed);
   }

   
   protected void onDataRemoved(String id) {
   }

   public void fillNames(List<String> names) {
      String value = this.requestedDataId != null ? this.requestedDataId : (this.data == null ? null : this.data.getId());
      this.namesList.fill(names);
      this.namesList.sort();
      this.namesList.setCurrentFile(value);
   }

   protected GuiScrollElement createScrollEditor() {
      GuiScrollElement scrollEditor = new GuiScrollElement(this.mc);
      scrollEditor.flex().relative(this.editor).wh(1.0F, 1.0F).column(5).stretch().vertical().scroll().padding(10);
      return scrollEditor;
   }

   public void open() {
      super.open();
      this.update = true;
      this.save = true;
   }

   public void appear() {
      super.appear();
      if (this.update) {
         this.update = false;
         this.requestDataNames();
      }

      if (this.data != null) {
         this.requestedDataId = this.data.getId();
         Dispatcher.sendToServer(new PacketContentRequestData(this.getType(), this.data.getId()));
      }

   }

   public void requestDataNames() {
      Dispatcher.sendToServer(new PacketContentRequestNames(this.getType()));
   }

   public void disappear() {
      super.disappear();
      if (this.save) {
         this.save();
      }

   }

   public void close() {
      super.close();
      if (this.save) {
         this.save();
      }

   }

   public void save() {
      if (!this.update && this.data != null && this.editor.isEnabled()) {
         this.preSave();
         Dispatcher.sendToServer(new PacketContentData(this.getType(), this.data.getId(), (class_2487)this.data.serializeNBT()));
      }

   }

   protected void preSave() {
   }

   public void draw(GuiContext context) {
      this.iconBar.area.draw(1996488704);
      GuiDraw.drawHorizontalGradientRect(this.iconBar.area.x - 6, this.iconBar.area.y, this.iconBar.area.x, this.iconBar.area.ey(), 0, 687865856);
      if (this.sidebar.isVisible()) {
         this.sidebar.area.draw(-1442840576);
      }

      super.draw(context);
      if (!this.editor.isEnabled() && this.data != null) {
         GuiDraw.drawLockedArea(this.editor);
      }

   }
}
