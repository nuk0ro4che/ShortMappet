package mchorse.mappet.api.quests.rewards;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.compat.NbtCompat;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2540;

public class ItemStackReward implements IReward {
   public List<class_1799> stacks = new ArrayList();

   public ItemStackReward(class_1799... stacks) {
      for(class_1799 stack : stacks) {
         this.stacks.add(stack);
      }

   }

   public void reward(class_1657 player) {
      for(class_1799 stack : this.stacks) {
         class_1799 copy = stack.method_7972();
         if (!player.method_31548().method_7394(copy) && !copy.method_7960()) {
            player.method_7328(copy, false);
         }
      }

   }

   public IReward copy() {
      ItemStackReward reward = new ItemStackReward(new class_1799[0]);

      for(class_1799 stack : this.stacks) {
         reward.stacks.add(stack.method_7972());
      }

      return reward;
   }

   public String getType() {
      return "item";
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 items = new class_2499();
      tag.method_10566("Items", items);

      for(class_1799 stack : this.stacks) {
         if (!stack.method_7960()) {
            items.add(NbtCompat.write(stack));
         }
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Items")) {
         class_2499 items = tag.method_10554("Items", 10);

         for(int i = 0; i < items.size(); ++i) {
            class_1799 stack = class_1799.method_7915(items.method_10602(i));
            if (!stack.method_7960()) {
               this.stacks.add(stack);
            }
         }
      }

   }

   public void fromBytes(ByteBuf buf) {
      int i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         this.stacks.add((new class_2540(buf)).method_10819());
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.stacks.size());

      for(class_1799 stack : this.stacks) {
         (new class_2540(buf)).method_10793(stack);
      }

   }
}
