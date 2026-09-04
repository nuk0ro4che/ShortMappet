package mchorse.mappet.api.quests.objectives;

import mchorse.mappet.compat.NbtCompat;
import mchorse.mappet.utils.InventoryUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2487;

public class CollectObjective extends AbstractObjective {
   public class_1799 stack;
   public boolean ignoreNBT;

   public CollectObjective() {
      this.stack = class_1799.field_8037;
   }

   public CollectObjective(class_1799 stack) {
      this.stack = class_1799.field_8037;
      this.stack = stack == null ? class_1799.field_8037 : stack;
   }

   public boolean isComplete(class_1657 player) {
      return this.countItems(player) >= this.stack.method_7947();
   }

   public void complete(class_1657 player) {
      InventoryUtils.removeItems(player, this.stack, this.stack.method_7947(), this.ignoreNBT);
   }

   private int countItems(class_1657 player) {
      return InventoryUtils.countItems(player, this.stack, true, this.ignoreNBT);
   }

   @Environment(EnvType.CLIENT)
   public String stringifyObjective(class_1657 player) {
      String name = this.stack.method_7964().getString();
      int count = Math.min(this.countItems(player), this.stack.method_7947());
      return !this.message.isEmpty() ? this.message.replace("${name}", name).replace("${count}", String.valueOf(count)).replace("${total}", String.valueOf(this.stack.method_7947())) : class_1074.method_4662("mappet.gui.quests.objective_collect.string", new Object[]{name, count, this.stack.method_7947()});
   }

   public String getType() {
      return "collect";
   }

   public class_2487 partialSerializeNBT() {
      return new class_2487();
   }

   public void partialDeserializeNBT(class_2487 tag) {
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      if (!this.stack.method_7960()) {
         tag.method_10566("Item", NbtCompat.write(this.stack));
      }

      tag.method_10556("IgnoreNBT", this.ignoreNBT);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Item")) {
         this.stack = class_1799.method_7915(tag.method_10562("Item"));
      }

      this.ignoreNBT = tag.method_10577("IgnoreNBT");
   }
}
