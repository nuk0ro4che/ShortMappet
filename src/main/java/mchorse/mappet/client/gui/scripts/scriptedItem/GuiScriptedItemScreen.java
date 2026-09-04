package mchorse.mappet.client.gui.scripts.scriptedItem;

import java.util.function.Consumer;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mappet.common.ScriptedItemProps;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.items.PacketScriptedItemInfo;
import mchorse.mappet.utils.NBTUtils;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.list.GuiLabelListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.Label;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_2561.class_2562;

public class GuiScriptedItemScreen extends GuiBase {
   public GuiFormattedTextElement title;
   public GuiElement lore;
   public GuiIconElement addLore;
   public GuiScrollElement editor;
   public GuiLabelListElement<String> triggers;
   public GuiTriggerElement trigger;
   private String lastTrigger = "interact_with_air";
   private final class_1799 stack;
   private final ScriptedItemProps props;

   private static String encodeLore(String lore) {
      return class_2562.method_10867(class_2561.method_43470(lore));
   }

   private static String decodeLore(String lore) {
      try {
         class_2561 text = class_2562.method_10877(lore);
         return text == null ? lore : text.getString();
      } catch (Exception var2) {
         return lore;
      }
   }

   public GuiScriptedItemScreen(class_310 mc, class_1799 stack) {
      this.stack = stack;
      this.props = NBTUtils.getScriptedItemProps(stack);
      GuiLabel triggersLabel = Elements.label(IKey.lang("mappet.gui.scripted_item.title")).anchor(0.0F, 0.5F).background();
      GuiLabel titleLabel = Elements.label(IKey.lang("mappet.gui.scripted_item.item_title")).anchor(0.0F, 0.5F).background();
      GuiLabel loreLabel = Elements.label(IKey.lang("mappet.gui.scripted_item.item_lore")).anchor(0.0F, 0.5F).background();
      this.title = new GuiFormattedTextElement(mc, (value) -> stack.method_7977(class_2561.method_43470(value)));
      this.title.flex().relative(this.viewport).x(10).w(0.5F, -20).y(35).h(32);
      this.lore = Elements.column(mc, 5, new GuiElement[0]);
      this.lore.flex().relative(loreLabel).y(1.0F, 8).w(1.0F).hTo(this.viewport, 1.0F, -10);
      this.addLore = new GuiIconElement(mc, Icons.ADD, (b) -> this.addLore(mc, ""));
      this.addLore.flex().relative(loreLabel).x(1.0F, 3).y(-3).anchorX(1.0F);
      this.addLore.tooltip(IKey.lang("mappet.gui.scripted_item.item_lore_add"));
      this.editor = new GuiScrollElement(mc);
      this.editor.flex().relative(this.viewport).x(0.5F).y(281).w(0.5F).h(1.0F, -311).column(5).scroll().stretch().padding(10);
      this.triggers = new GuiLabelListElement<String>(mc, (l) -> this.fillTrigger(mc, (Label)l.get(0), false));
      this.triggers.background().flex().relative(this.viewport).x(0.5F, 10).y(35).w(0.5F, -20).h(246);
      this.trigger = (new GuiTriggerElement(mc)).onClose(this::updateCurrentTrigger);
      this.trigger.flex().relative(this.viewport).x(1.0F, -10).y(1.0F, -10).wh(120, 20).anchor(1.0F, 1.0F);
      triggersLabel.flex().relative(this.viewport).x(0.5F, 10).y(10).wh(120, 20);
      titleLabel.flex().relative(this.viewport).x(10).y(10).wh(120, 20);
      loreLabel.flex().relative(this.title).y(1.0F, 25).w(1.0F).h(20);
      this.fill(mc);
      this.root.add(this.title);
      this.root.add(new IGuiElement[]{this.triggers, this.editor, this.trigger, triggersLabel, titleLabel, loreLabel, this.lore, this.addLore});
   }

   private void addLore(class_310 mc, String lore) {
      GuiFormattedTextElement textElement = new GuiFormattedTextElement(mc, (Consumer)null);
      textElement.text.setText(lore);
      textElement.context(() -> {
         GuiSimpleContextMenu menu = new GuiSimpleContextMenu(class_310.method_1551());
         menu.action(Icons.REMOVE, IKey.lang("mappet.gui.scripted_item.item_lore_remove"), () -> {
            textElement.removeFromParent();
            this.lore.resize();
         });
         return menu;
      });
      textElement.flex().h(32);
      this.lore.add(textElement);
      this.lore.resize();
   }

   private void fillTrigger(class_310 mc, Label<String> trigger, boolean select) {
      this.editor.removeAll();
      this.editor.add((new GuiText(mc)).text(IKey.lang("mappet.gui.scripted_item." + (String)trigger.value)));
      this.editor.add((new GuiText(mc)).text(IKey.lang("mappet.gui.scripted_item.descriptions." + (String)trigger.value)));
      this.trigger.set((Trigger)this.props.registered.get(trigger.value));
      if (select) {
         this.triggers.setCurrentScroll(trigger);
      }

      this.lastTrigger = (String)trigger.value;
      this.editor.resize();
   }

   private void updateCurrentTrigger() {
      Trigger trigger = (Trigger)this.props.registered.get(this.lastTrigger);
      ((Label)this.triggers.getCurrentFirst()).title = this.createTooltip(this.lastTrigger, trigger);
   }

   public IKey createTooltip(String key, Trigger trigger) {
      IKey title = IKey.lang("mappet.gui.scripted_item." + key);
      if (trigger.blocks.isEmpty()) {
         return title;
      } else {
         IKey count = IKey.str(" §7(§6" + trigger.blocks.size() + "§7)§r");
         return IKey.comp(new IKey[]{title, count});
      }
   }

   public void fill(class_310 mc) {
      this.triggers.clear();

      for(String key : this.props.registered.keySet()) {
         this.triggers.add(this.createTooltip(key, (Trigger)this.props.registered.get(key)), key);
      }

      this.triggers.sort();
      this.triggers.setCurrentValue(this.lastTrigger);
      this.fillTrigger(mc, (Label)this.triggers.getCurrentFirst(), true);
      this.triggers.resize();
      this.title.text.setText(this.stack.method_7964().getString());
      class_2487 tag = this.stack.method_7969();
      if (tag != null && tag.method_10573("display", 10) && tag.method_10562("display").method_10573("Lore", 9)) {
         class_2499 lore = tag.method_10562("display").method_10554("Lore", 8);

         for(int i = 0; i < lore.size(); ++i) {
            this.addLore(mc, decodeLore(lore.method_10608(i)));
         }
      }

   }

   public boolean method_25421() {
      return false;
   }

   protected void closeScreen() {
      super.closeScreen();
      this.props.pickedUp = false;
      class_2499 lore = new class_2499();

      for(GuiFormattedTextElement element : this.lore.getChildren(GuiFormattedTextElement.class)) {
         String text = element.text.getText();
         if (!text.isEmpty()) {
            lore.add(class_2519.method_23256(encodeLore(text)));
         }
      }

      this.stack.method_7911("display").method_10566("Lore", lore);
      if (this.title.text.getText().trim().isEmpty()) {
         this.stack.method_7911("display").method_10551("Name");
      }

      Dispatcher.sendToServer(new PacketScriptedItemInfo(this.props.toNBT(), this.stack.method_7969(), 0));
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      this.method_25420(drawContext);
      super.method_25394(drawContext, mouseX, mouseY, partialTicks);
   }
}
