package mchorse.mappet.api.crafting;

import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.compat.NbtCompat;
import mchorse.mappet.utils.InventoryUtils;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

public class CraftingRecipe implements INBTSerializable<class_2487> {
   public String title = "";
   public String description = "";
   public class_2371<class_1799> input = class_2371.method_10211();
   public class_2371<class_1799> output = class_2371.method_10211();
   public Checker visible = new Checker(true);
   public int hotkey = -1;
   public Trigger trigger = new Trigger();
   public boolean ignoreNBT = true;

   public boolean isAvailable(class_1657 player) {
      return this.visible.check(new DataContext(player));
   }

   public boolean craft(class_1657 player) {
      return this.craft(player, (DataContext)null);
   }

   public boolean craft(class_1657 player, DataContext context) {
      if (context == null) {
         context = new DataContext(player);
      }

      if (!this.isPlayerHasAllItems(player)) {
         return false;
      } else {
         for(class_1799 stack : this.input) {
            InventoryUtils.removeItems(player, stack, stack.method_7947(), this.ignoreNBT);
         }

         for(class_1799 stack : this.output) {
            this.addOrDrop(player, stack.method_7972());
         }

         this.trigger.trigger(context);
         return true;
      }
   }

   public boolean isPlayerHasAllItems(class_1657 player) {
      for(class_1799 stack : this.input) {
         if (InventoryUtils.countItems(player, stack, true, this.ignoreNBT) < stack.method_7947()) {
            return false;
         }
      }

      return true;
   }

   private void addOrDrop(class_1657 player, class_1799 stack) {
      boolean flag = player.method_31548().method_7394(stack);
      if (flag) {
         player.method_37908().method_43128((class_1657)null, player.method_23317(), player.method_23318(), player.method_23321(), class_3417.field_15197, class_3419.field_15248, 0.2F, 1.0F);
         player.field_7512.method_7623();
      } else {
         class_1542 entityitem = player.method_7328(stack, false);
         if (entityitem != null) {
            entityitem.method_6982(0);
            entityitem.method_48349(player.method_5667());
         }
      }

   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 input = this.serializeList(this.input);
      class_2499 output = this.serializeList(this.output);
      if (!this.title.isEmpty()) {
         tag.method_10582("Title", this.title);
      }

      if (!this.description.isEmpty()) {
         tag.method_10582("Description", this.description);
      }

      if (input != null) {
         tag.method_10566("Input", input);
      }

      if (output != null) {
         tag.method_10566("Output", output);
      }

      tag.method_10566("Visible", this.visible.serializeNBT());
      class_2487 trigger = this.trigger.serializeNBT();
      if (trigger.method_10546() > 0) {
         tag.method_10566("Trigger", trigger);
      }

      if (this.hotkey > 0) {
         tag.method_10569("Hotkey", this.hotkey);
      }

      tag.method_10556("IgnoreNBT", this.ignoreNBT);
      return tag;
   }

   private class_2499 serializeList(class_2371<class_1799> list) {
      class_2499 tagList = new class_2499();

      for(class_1799 stack : list) {
         if (!stack.method_7960()) {
            tagList.add(NbtCompat.write(stack));
         }
      }

      return tagList.size() == 0 ? null : tagList;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Title")) {
         this.title = tag.method_10558("Title");
      }

      if (tag.method_10545("Description")) {
         this.description = tag.method_10558("Description");
      }

      if (tag.method_10545("Input")) {
         this.deserializeList(this.input, tag.method_10554("Input", 10));
      }

      if (tag.method_10545("Output")) {
         this.deserializeList(this.output, tag.method_10554("Output", 10));
      }

      if (tag.method_10545("Visible")) {
         this.visible.deserializeNBT(tag.method_10580("Visible"));
      }

      if (tag.method_10545("Trigger")) {
         this.trigger.deserializeNBT(tag.method_10562("Trigger"));
      }

      if (tag.method_10545("Hotkey")) {
         this.hotkey = tag.method_10550("Hotkey");
      }

      if (tag.method_10545("IgnoreNBT")) {
         this.ignoreNBT = tag.method_10577("IgnoreNBT");
      }

   }

   private void deserializeList(class_2371<class_1799> list, class_2499 tagList) {
      list.clear();

      for(int i = 0; i < tagList.size(); ++i) {
         class_2487 compound = tagList.method_10602(i);
         class_1799 stack = class_1799.method_7915(compound);
         if (!stack.method_7960()) {
            list.add(stack);
         }
      }

   }
}
