package mchorse.mappet.client.gui.utils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1799;
import net.minecraft.class_310;

public class GuiItemsElement extends GuiElement {
   public GuiElement stacks;
   private List<class_1799> items;

   public GuiItemsElement(class_310 mc, IKey title, List<class_1799> items) {
      super(mc);
      GuiLabel label = Elements.label(title);
      GuiIconElement add = new GuiIconElement(mc, Icons.ADD, (b) -> {
         this.items.add(class_1799.field_8037);
         this.addItem(class_1799.field_8037);
         this.getParentContainer().resize();
      });
      add.flex().wh(10, 8);
      Objects.requireNonNull(this.font);
      GuiElement row = Elements.row(mc, 5, 0, 9, new GuiElement[]{label, add});
      this.stacks = new GuiElement(mc);
      label.flex().h(0);
      row.flex().row(5).preferred(0);
      this.stacks.flex().grid(5).width(24).resizes(true);
      this.flex().column(5).vertical().stretch();
      this.add(new IGuiElement[]{row, this.stacks});
      this.set(items);
   }

   public void set(List<class_1799> items) {
      this.stacks.removeAll();
      this.items = items;
      if (this.items != null) {
         for(class_1799 stack : this.items) {
            this.addItem(stack);
         }
      }

   }

   public void addItem(class_1799 stack) {
      GuiSlotElement slot = new GuiSlotElement(this.mc, 0, (Consumer)null);
      slot.callback = (item) -> {
         int index = this.stacks.getChildren().indexOf(slot);
         if (index != -1) {
            this.items.set(index, item.method_7972());
         }

      };
      slot.setStack(stack);
      slot.context(() -> slot.createDefaultSlotContextMenu().action(Icons.REMOVE, IKey.lang("mappet.gui.items.context.remove"), () -> {
            int index = this.stacks.getChildren().indexOf(slot);
            if (index != -1) {
               this.items.remove(index);
               slot.removeFromParent();
               this.getParentContainer().resize();
            }

         }, 16711731));
      this.stacks.add(slot);
   }
}
