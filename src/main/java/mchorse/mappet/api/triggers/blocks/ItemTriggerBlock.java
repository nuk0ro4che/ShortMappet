package mchorse.mappet.api.triggers.blocks;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.Target;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.compat.NbtCompat;
import mchorse.mappet.utils.EnumUtils;
import mchorse.mappet.utils.InventoryUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2487;

public class ItemTriggerBlock extends AbstractTriggerBlock {
   public Target target;
   public class_1799 stack;
   public ItemMode mode;
   public boolean ignoreNBT;

   public ItemTriggerBlock() {
      this.target = new Target(TargetMode.SUBJECT);
      this.stack = class_1799.field_8037;
      this.mode = ItemTriggerBlock.ItemMode.TAKE;
      this.ignoreNBT = true;
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      String displayName = this.stack.method_7964().getString();
      if (this.stack.method_7947() > 1) {
         displayName = displayName + String.valueOf(class_124.field_1065) + " (" + String.valueOf(class_124.field_1080) + this.stack.method_7947() + String.valueOf(class_124.field_1065) + ")";
      }

      if (this.mode == ItemTriggerBlock.ItemMode.GIVE) {
         return class_1074.method_4662("mappet.gui.nodes.item.give", new Object[]{displayName});
      } else {
         if (!this.ignoreNBT) {
            displayName = displayName + String.valueOf(class_124.field_1064) + String.valueOf(class_124.field_1056) + " (+NBT)";
         }

         return class_1074.method_4662("mappet.gui.nodes.item.take", new Object[]{displayName});
      }
   }

   public void trigger(DataContext context) {
      class_1657 player;
      if (!this.stack.method_7960() && (player = this.target.getPlayer(context)) != null) {
         if (this.mode == ItemTriggerBlock.ItemMode.GIVE) {
            class_1799 copy = this.stack.method_7972();
            if (!player.method_31548().method_7394(copy) && !copy.method_7960()) {
               player.method_7328(copy, false);
            }

         } else {
            if (InventoryUtils.countItems(player, this.stack, true, this.ignoreNBT) >= this.stack.method_7947()) {
               if (this.ignoreNBT) {
                  Object var10000 = null;
               } else {
                  this.stack.method_7969();
               }

               InventoryUtils.removeItems(player, this.stack, this.stack.method_7947(), this.ignoreNBT);
            } else {
               context.cancel();
            }

         }
      } else {
         context.cancel();
      }
   }

   public boolean isEmpty() {
      return this.stack.method_7960();
   }

   protected void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10566("Target", this.target.serializeNBT());
      tag.method_10566("Stack", NbtCompat.write(this.stack));
      tag.method_10569("Mode", this.mode.ordinal());
      tag.method_10556("IgnoreNBT", this.ignoreNBT);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Target")) {
         this.target.deserializeNBT(tag.method_10562("Target"));
      }

      if (tag.method_10545("Stack")) {
         this.stack = class_1799.method_7915(tag.method_10562("Stack"));
      }

      if (tag.method_10545("Mode")) {
         this.mode = (ItemMode)EnumUtils.getValue(tag.method_10550("Mode"), ItemTriggerBlock.ItemMode.values(), ItemTriggerBlock.ItemMode.TAKE);
      }

      if (tag.method_10545("IgnoreNBT")) {
         this.ignoreNBT = tag.method_10577("IgnoreNBT");
      }

   }

   public static enum ItemMode {
      TAKE,
      GIVE;
      private static ItemMode[] $values() {
         return new ItemMode[]{TAKE, GIVE};
      }
   }
}
