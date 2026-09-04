package mchorse.mappet.client.gui.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.ClientProxy;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mappet.client.gui.utils.overlays.GuiContentNamesOverlayPanel;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mappet.client.gui.utils.overlays.GuiStringOverlayPanel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiLibrariesOverlayPanel extends GuiStringOverlayPanel {
   private List<String> libraries;
   private String main;

   public GuiLibrariesOverlayPanel(class_310 mc, Script script) {
      super(mc, IKey.lang("mappet.gui.scripts.libraries.title"), false, script.libraries, (Consumer)null);
      this.libraries = script.libraries;
      this.main = script.getId();
      this.strings.context(() -> {
         GuiSimpleContextMenu menu = new GuiSimpleContextMenu(mc);
         menu.action(Icons.ADD, IKey.lang("mappet.gui.scripts.libraries.context.add"), this::addLibrary);
         if (!this.strings.list.isDeselected()) {
            menu.action(Icons.REMOVE, IKey.lang("mappet.gui.scripts.libraries.context.remove"), this::removeLibrary, 16711731);
         }

         return menu.shadow();
      });
      this.strings.list.sorting();
   }

   private void addLibrary() {
      ContentType type = ContentType.SCRIPTS;
      ClientProxy.requestNames(type, (names) -> {
         for(String string : this.strings.list.getList()) {
            names.remove(string);
         }

         for(String string : new ArrayList<>(names)) {
            if (!string.endsWith(this.main.substring(this.main.lastIndexOf(".")))) {
               names.remove(string);
            }
         }

         names.remove(this.main);
         GuiContentNamesOverlayPanel overlay = new GuiContentNamesOverlayPanel(class_310.method_1551(), type.getPickLabel(), type, names, (Consumer)null) {
            public void onClose() {
               String library = this.getValue();
               if (library != null && !library.isEmpty()) {
                  GuiLibrariesOverlayPanel.this.strings.list.add(library);
                  GuiLibrariesOverlayPanel.this.strings.list.setCurrentScroll(library);
               }

               super.onClose();
            }
         };
         GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.5F, 0.7F);
      });
   }

   private void removeLibrary() {
      int index = this.strings.list.getIndex();
      String key = (String)this.strings.list.getCurrentFirst();
      this.strings.list.remove(key);
      this.strings.list.setIndex(Math.max(index - 1, 0));
   }

   public void onClose() {
      this.libraries.clear();
      this.libraries.addAll(this.strings.list.getList());
      super.onClose();
   }

   protected void drawBackground(GuiContext context) {
      super.drawBackground(context);
      if (this.strings.list.getList().size() <= 1) {
         GuiMappetUtils.drawRightClickHere(context, this.area);
      }

   }
}
