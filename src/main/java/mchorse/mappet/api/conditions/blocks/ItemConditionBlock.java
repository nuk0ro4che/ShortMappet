package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.compat.NbtCompat;
import mchorse.mappet.utils.EnumUtils;
import mchorse.mappet.utils.InventoryUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2487;

public class ItemConditionBlock extends TargetConditionBlock {
   public class_1799 stack;
   public ItemCheck check;

   public ItemConditionBlock() {
      this.stack = class_1799.field_8037;
      this.check = ItemConditionBlock.ItemCheck.HELD;
   }

   public boolean evaluateBlock(DataContext context) {
      if (this.target.mode != TargetMode.GLOBAL) {
         class_1657 player = this.target.getPlayer(context);
         if (player != null) {
            if (this.check != ItemConditionBlock.ItemCheck.HELD) {
               if (this.check == ItemConditionBlock.ItemCheck.EQUIPMENT) {
                  return InventoryUtils.countItems(player.method_31548().field_7548, this.stack, true) >= this.stack.method_7947();
               }

               return InventoryUtils.countItems(player.method_31548().field_7547, this.stack, true) >= this.stack.method_7947();
            }

            class_1799 main = player.method_6118(class_1304.field_6173);
            class_1799 off = player.method_6118(class_1304.field_6171);
            boolean a = InventoryUtils.areStacksSimilar(main, this.stack);
            boolean b = InventoryUtils.areStacksSimilar(off, this.stack);
            return a || b;
         }
      }

      return false;
   }

   protected TargetMode getDefaultTarget() {
      return TargetMode.SUBJECT;
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      String name = this.stack.method_7964().getString();
      if (this.check == ItemConditionBlock.ItemCheck.HELD) {
         return class_1074.method_4662("mappet.gui.conditions.item.holds", new Object[]{name});
      } else {
         return this.check == ItemConditionBlock.ItemCheck.EQUIPMENT ? class_1074.method_4662("mappet.gui.conditions.item.equipment", new Object[]{name}) : class_1074.method_4662("mappet.gui.conditions.item.inventory", new Object[]{name + " x" + this.stack.method_7947()});
      }
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10566("Stack", NbtCompat.write(this.stack));
      tag.method_10569("Check", this.check.ordinal());
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.stack = class_1799.method_7915(tag.method_10562("Stack"));
      this.check = (ItemCheck)EnumUtils.getValue(tag.method_10550("Check"), ItemConditionBlock.ItemCheck.values(), ItemConditionBlock.ItemCheck.HELD);
   }

   public static enum ItemCheck {
      HELD,
      EQUIPMENT,
      INVENTORY;
      private static ItemCheck[] $values() {
         return new ItemCheck[]{HELD, EQUIPMENT, INVENTORY};
      }
   }
}
