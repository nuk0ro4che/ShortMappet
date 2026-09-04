package mchorse.mappet.client.gui.events;

import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.api.misc.hotkeys.TriggerHotkey;
import mchorse.mappet.api.misc.hotkeys.TriggerHotkeys;
import mchorse.mappet.client.gui.conditions.GuiCheckerElement;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiKeybindElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Keys;
import net.minecraft.class_310;

public class GuiTriggerHotkeysOverlayPanel extends GuiOverlayPanel {
   public GuiTriggerHotkeyList list;
   public GuiScrollElement editor;
   public GuiKeybindElement key;
   public GuiToggleElement mouse;
   public GuiButtonElement mouseButton;
   public GuiToggleElement toggle;
   public GuiTriggerElement trigger;
   public GuiCheckerElement enabled;
   private TriggerHotkeys hotkeys;
   private TriggerHotkey hotkey;

   public GuiTriggerHotkeysOverlayPanel(class_310 mc, TriggerHotkeys hotkeys) {
      super(mc, IKey.lang("mappet.gui.nodes.event.hotkeys.title"));
      this.hotkeys = hotkeys;
      this.list = new GuiTriggerHotkeyList(mc, (l) -> this.pickHotkey((TriggerHotkey)l.get(0), false));
      this.list.sorting().setList(hotkeys.hotkeys);
      this.list.context(() -> {
         GuiSimpleContextMenu menu = (new GuiSimpleContextMenu(this.mc)).action(Icons.ADD, IKey.lang("mappet.gui.nodes.event.hotkeys.context.add"), this::addHotkey);
         if (!this.hotkeys.hotkeys.isEmpty()) {
            menu.action(Icons.REMOVE, IKey.lang("mappet.gui.nodes.event.hotkeys.context.remove"), this::removeHotkey, 16711731);
         }

         return menu.shadow();
      });
      this.editor = new GuiScrollElement(mc);
      this.key = new GuiKeybindElement(mc, (k) -> {
         if (k == 1) {
            this.hotkey.keycode = 0;
            this.key.setKeybind(0);
         } else {
            this.hotkey.keycode = k;
         }

      });
      this.mouse = new GuiToggleElement(mc, IKey.lang("mappet.gui.nodes.event.hotkeys.mouse"), (b) -> {
         if (this.hotkey != null) {
            this.hotkey.input = b.isToggled() ? TriggerHotkey.INPUT_MOUSE : TriggerHotkey.INPUT_KEYBOARD;
            this.updateInputWidgets();
            this.list.update();
         }
      });
      this.mouseButton = new GuiButtonElement(mc, IKey.lang("mappet.gui.nodes.event.hotkeys.mouse.0"), (b) -> {
         if (this.hotkey != null) {
            this.hotkey.keycode = this.hotkey.keycode + 1;
            if (this.hotkey.keycode > 4 || this.hotkey.keycode < 0) {
               this.hotkey.keycode = 0;
            }

            this.updateInputWidgets();
            this.list.update();
         }
      });
      this.toggle = new GuiToggleElement(mc, IKey.lang("mappet.gui.nodes.event.hotkeys.toggle"), (b) -> this.hotkey.toggle = b.isToggled());
      this.toggle.tooltip(IKey.lang("mappet.gui.nodes.event.hotkeys.toggle_tooltip"));
      this.trigger = new GuiTriggerElement(mc);
      this.enabled = new GuiCheckerElement(mc);
      this.list.flex().relative(this.content).w(120).h(1.0F);
      this.editor.flex().relative(this.content).x(120).w(1.0F, -120).h(1.0F).column(5).vertical().stretch().scroll().padding(10);
      this.editor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.nodes.event.hotkeys.key")), this.key, this.mouse, this.mouseButton, this.toggle});
      this.editor.add(this.trigger.marginTop(12));
      this.editor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.nodes.event.hotkeys.enabled")).marginTop(12), this.enabled});
      this.content.add(new IGuiElement[]{this.editor, this.list});
      this.pickHotkey(hotkeys.hotkeys.isEmpty() ? null : (TriggerHotkey)hotkeys.hotkeys.get(0), true);
   }

   private void addHotkey() {
      TriggerHotkey hotkey = new TriggerHotkey();
      this.hotkeys.hotkeys.add(hotkey);
      this.pickHotkey(hotkey, true);
      this.list.update();
   }

   private void removeHotkey() {
      int index = this.list.getIndex();
      this.hotkeys.hotkeys.remove(index);
      this.pickHotkey(this.hotkeys.hotkeys.isEmpty() ? null : (TriggerHotkey)this.hotkeys.hotkeys.get(Math.max(index - 1, 0)), true);
      this.list.update();
   }

   private void pickHotkey(TriggerHotkey hotkey, boolean select) {
      this.hotkey = hotkey;
      this.editor.setVisible(hotkey != null);
      if (hotkey != null) {
         this.key.setKeybind(hotkey.keycode);
         this.mouse.toggled(hotkey.input == TriggerHotkey.INPUT_MOUSE);
         this.updateInputWidgets();
         this.toggle.toggled(hotkey.toggle);
         this.trigger.set(hotkey.trigger);
         this.enabled.set(hotkey.enabled);
         if (select) {
            this.list.setCurrentScroll(hotkey);
         }
      }

   }

   private void updateInputWidgets() {
      boolean mouseInput = this.hotkey != null && this.hotkey.input == TriggerHotkey.INPUT_MOUSE;
      this.key.setVisible(!mouseInput);
      this.mouseButton.setVisible(mouseInput);
      if (mouseInput) {
         this.mouseButton.label = IKey.lang("mappet.gui.nodes.event.hotkeys.mouse." + this.hotkey.keycode);
      }
   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (this.hotkeys.hotkeys.isEmpty()) {
         GuiMappetUtils.drawRightClickHere(context, this.list.area);
      }

   }

   public static class GuiTriggerHotkeyList extends GuiListElement<TriggerHotkey> {
      public GuiTriggerHotkeyList(class_310 mc, Consumer<List<TriggerHotkey>> callback) {
         super(mc, callback);
      }

      protected String elementToString(TriggerHotkey element) {
         if (element.input == TriggerHotkey.INPUT_MOUSE) {
            return IKey.lang("mappet.gui.nodes.event.hotkeys.mouse." + element.keycode).get();
         }

         return Keys.getKeyName(element.keycode);
      }
   }
}
