package mchorse.mappet.api.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mappet.api.scripts.code.ScriptEvent;
import mchorse.mappet.api.scripts.code.mappet.MappetUIContext;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.ui.PacketUIData;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class UIContext {
   public class_2487 data = new class_2487();
   public class_1657 player;
   public UI ui;
   private String script = "";
   private String function = "";
   @Environment(EnvType.CLIENT)
   private Map<String, GuiElement> elements;
   @Environment(EnvType.CLIENT)
   private Set<String> reservedData;
   private String last = "";
   private boolean closed;
   private String hotkey = "";
   private String context = "";
   private String hovered = "";
   private String unhovered = "";
   private Long dirty;
   @Environment(EnvType.CLIENT)
   public boolean editorPreview;

   public UIContext(UI ui) {
      this.ui = ui;
   }

   public UIContext(UI ui, class_1657 player, String script, String function) {
      this.ui = ui;
      this.player = player;
      this.script = script == null ? "" : script;
      this.function = function == null ? "" : function;
   }

   public UIComponent getById(String id) {
      return this.getByIdRecursive(id, this.ui.root);
   }

   private UIComponent getByIdRecursive(String id, UIComponent component) {
      for(UIComponent child : component.getChildComponents()) {
         if (child.id.equals(id)) {
            return child;
         }

         UIComponent result = this.getByIdRecursive(id, child);
         if (result != null) {
            return result;
         }
      }

      return null;
   }

   public void clearChanges() {
      this.clearChangesRecursive(this.ui.root);
   }

   private void clearChangesRecursive(UIComponent component) {
      for(UIComponent child : component.getChildComponents()) {
         child.clearChanges();
         this.clearChangesRecursive(child);
      }

   }

   public class_2487 compileChanges() {
      class_2487 tag = new class_2487();
      this.compileChangesRecursive(tag, this.ui.root);
      return tag;
   }

   private void compileChangesRecursive(class_2487 tag, UIComponent component) {
      for(UIComponent child : component.getChildComponents()) {
         if (!child.id.isEmpty()) {
            this.compileComponent(tag, child);
         }

         this.compileChangesRecursive(tag, child);
      }

   }

   private void compileComponent(class_2487 tag, UIComponent component) {
      Set<String> changes = component.getChanges();
      if (!changes.isEmpty()) {
         class_2487 full = component.serializeNBT();
         class_2487 partial = new class_2487();

         for(String key : changes) {
            if (full.method_10545(key)) {
               partial.method_10566(key, full.method_10580(key));
            }
         }

         tag.method_10566(component.id, partial);
      }
   }

   public void populateDefaultData() {
      this.populateDefaultDataRecursive(this.ui.root);
   }

   private void populateDefaultDataRecursive(UIComponent component) {
      for(UIComponent child : component.getChildComponents()) {
         child.populateData(this.data);
         this.populateDefaultDataRecursive(child);
      }

   }

   public String getLast() {
      return this.last;
   }

   public String getHotkey() {
      return this.hotkey;
   }

   public String getContext() {
      return this.context;
   }

   public String getHovered() {
      return this.hovered;
   }

   public String getUnhovered() {
      return this.unhovered;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public boolean isDirty() {
      if (this.dirty == null) {
         return false;
      } else {
         return System.currentTimeMillis() >= this.dirty;
      }
   }

   public boolean isDirtyInProgress() {
      return this.dirty != null;
   }

   @Environment(EnvType.CLIENT)
   public UIContext editorPreview() {
      this.editorPreview = true;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public void registerElement(String id, GuiElement element, boolean reserved) {
      if (this.elements == null) {
         this.elements = new HashMap();
      }

      this.elements.put(id, element);
      if (reserved) {
         if (this.reservedData == null) {
            this.reservedData = new HashSet();
         }

         this.reservedData.add(id);
      }

   }

   @Environment(EnvType.CLIENT)
   public GuiElement getElement(String target) {
      return this.elements == null ? null : (GuiElement)this.elements.get(target);
   }

   @Environment(EnvType.CLIENT)
   public void sendKey(String action) {
      if (this.dirty != null) {
         this.sendToServer();
      } else {
         class_2487 tag = new class_2487();
         tag.method_10582("Hotkey", action);
         Dispatcher.sendToServer(new PacketUIData(tag));
      }

   }

   @Environment(EnvType.CLIENT)
   public void sendContext(String action) {
      if (this.dirty != null) {
         this.sendToServer();
      } else {
         class_2487 tag = new class_2487();
         tag.method_10582("Context", action);
         Dispatcher.sendToServer(new PacketUIData(tag));
      }

   }

   @Environment(EnvType.CLIENT)
   public void sendHover(String id) {
      class_2487 tag = new class_2487();
      tag.method_10582("Hovered", id);
      Dispatcher.sendToServer(new PacketUIData(tag));
   }

   @Environment(EnvType.CLIENT)
   public void sendUnhover(String id) {
      class_2487 tag = new class_2487();
      tag.method_10582("Unhovered", id);
      Dispatcher.sendToServer(new PacketUIData(tag));
   }

   @Environment(EnvType.CLIENT)
   public void sendComponentEvent(String event, String id, int mouseX, int mouseY) {
      if (id == null || id.isEmpty()) {
         return;
      }
      class_2487 tag = new class_2487();
      tag.method_10582("ComponentEvent", event);
      tag.method_10582("Component", id);
      tag.method_10569("MouseX", mouseX);
      tag.method_10569("MouseY", mouseY);
      Dispatcher.sendToServer(new PacketUIData(tag));
   }

   @Environment(EnvType.CLIENT)
   public void dirty(String id, long delay) {
      this.last = id;
      if (delay <= 0L) {
         this.dirty = null;
         this.sendToServer();
      } else {
         this.dirty = System.currentTimeMillis() + delay;
      }

   }

   @Environment(EnvType.CLIENT)
   public void sendToServer() {
      this.dirty = null;
      class_2487 tag = new class_2487();
      tag.method_10566("Data", this.data);
      tag.method_10582("Last", this.last);
      tag.method_10582("Hotkey", this.hotkey);
      tag.method_10582("Context", this.context);
      class_2487 oldData = this.data;
      this.data = new class_2487();
      if (this.reservedData != null) {
         for(String key : this.reservedData) {
            if (oldData.method_10545(key)) {
               this.data.method_10566(key, oldData.method_10580(key));
            }
         }
      }

      this.hotkey = "";
      Dispatcher.sendToServer(new PacketUIData(tag));
   }

   public void handleNewData(class_2487 data) {
      if (this.player != null) {
         this.data.method_10543(data.method_10562("Data"));
         this.last = data.method_10558("Last");
         this.hotkey = data.method_10558("Hotkey");
         this.context = data.method_10558("Context");
         this.hovered = data.method_10558("Hovered");
         this.unhovered = data.method_10558("Unhovered");
         boolean eventHandled = this.handleComponentEvent(data);
         boolean handlerHandled = !eventHandled && this.handleScript(this.player);
         if (eventHandled || handlerHandled) {
            this.sendToPlayer();
         } else {
            this.clearChanges();
         }

      }
   }

   public void sendToPlayer() {
      class_2487 changes = this.compileChanges();
      if (!changes.method_10541().isEmpty()) {
         Dispatcher.sendTo(new PacketUIData(changes), (class_3222)this.player);
      }

      this.clearChanges();
   }

   public void close() {
      


      if (this.player != null && !this.closed) {
         this.closed = true;
         this.last = "";
         this.handleScript(this.player);
      }
   }

   private boolean handleComponentEvent(class_2487 data) {
      String event = data.method_10545("ComponentEvent") ? data.method_10558("ComponentEvent") : (this.last.isEmpty() ? "" : "Callback");
      String id = data.method_10545("Component") ? data.method_10558("Component") : this.last;
      UIComponent component = id.isEmpty() ? null : this.getById(id);
      if (component == null || event.isEmpty()) {
         return false;
      }
      String code = component.getEventScript(event);
      if (code.isEmpty() || this.script.isEmpty()) {
         return false;
      }
      try {
         DataContext dataContext = new DataContext(this.player);
         ScriptEvent scriptEvent = new ScriptEvent(dataContext, this.script, event);
         int mouseX = data.method_10545("MouseX") ? data.method_10550("MouseX") : -1;
         int mouseY = data.method_10545("MouseY") ? data.method_10550("MouseY") : -1;
         Mappet.scripts.executeUIEvent(this.script, code, dataContext, scriptEvent, component, new MappetUIContext(this), id, mouseX, mouseY);
         return true;
      } catch (Exception exception) {
         exception.printStackTrace();
         return false;
      }
   }

   private boolean handleScript(class_1657 player) {
      if (!this.script.isEmpty() && !this.function.isEmpty()) {
         try {
            Mappet.scripts.execute(this.script, this.function, new DataContext(player));
            return true;
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         }
      } else {
         return false;
      }
   }
}
