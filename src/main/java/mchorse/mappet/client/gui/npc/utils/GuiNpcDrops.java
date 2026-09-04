package mchorse.mappet.client.gui.npc.utils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import mchorse.mappet.api.npcs.NpcDrop;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiNpcDrops extends GuiElement {
   public GuiElement element;
   private List<NpcDrop> drops;

   public GuiNpcDrops(class_310 mc) {
      super(mc);
      GuiLabel label = Elements.label(IKey.lang("mappet.gui.npcs.drops.title"));
      GuiIconElement add = new GuiIconElement(mc, Icons.ADD, (b) -> this.addDrop(new NpcDrop(), true));
      add.flex().wh(10, 8);
      Objects.requireNonNull(this.font);
      GuiElement row = Elements.row(mc, 5, 0, 9, new GuiElement[]{label, add});
      this.element = new GuiElement(mc);
      row.flex().row(5).preferred(0);
      this.element.flex().column(5).vertical().stretch();
      this.flex().column(5).vertical().stretch();
      this.add(new IGuiElement[]{row, this.element});
   }

   private void addDrop(NpcDrop drop, boolean insert) {
      if (insert) {
         this.drops.add(drop);
      }

      GuiElement row = Elements.row(this.mc, 5, 0, 24, new GuiElement[0]);
      GuiSlotElement slot = new GuiSlotElement(this.mc, 0, (Consumer)null);
      GuiTrackpadElement chance = new GuiTrackpadElement(this.mc, (v) -> drop.chance = v.floatValue() / 100.0F);
      chance.setValue((double)(drop.chance * 100.0F));
      chance.limit((double)0.0F, (double)100.0F).flex().h(24);
      slot.callback = (stack) -> drop.stack = stack.method_7972();
      slot.setStack(drop.stack);
      row.context(() -> (new GuiSimpleContextMenu(this.mc)).action(Icons.REMOVE, IKey.lang("mappet.gui.npcs.drops.context.remove"), () -> {
            int index = this.element.getChildren().indexOf(row);
            if (index != -1) {
               this.drops.remove(index);
               row.removeFromParent();
               this.getParentContainer().resize();
            }

         }, 16711731));
      row.add(new IGuiElement[]{slot, chance});
      this.element.add(row);
      this.getParentContainer().resize();
   }

   public void set(List<NpcDrop> drops) {
      this.drops = drops;
      this.element.removeAll();
      if (drops != null) {
         for(NpcDrop drop : this.drops) {
            this.addDrop(drop, false);
         }
      }

   }
}
