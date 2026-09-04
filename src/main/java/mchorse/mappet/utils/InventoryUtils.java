package mchorse.mappet.utils;

import java.util.Objects;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_2487;

public class InventoryUtils {
   public static boolean areStacksSimilar(class_1799 a, class_1799 b) {
      return areStacksSimilar(a, b, false);
   }

   public static boolean areStacksSimilar(class_1799 a, class_1799 b, boolean ignoreNBT) {
      if (a.method_7960() && b.method_7960()) {
         return true;
      } else if (ignoreNBT) {
         return a.method_7909() == b.method_7909();
      } else if (a.method_7909() != b.method_7909()) {
         return false;
      } else {
         class_2487 an = a.method_7969() == null ? null : a.method_7969().method_10553();
         class_2487 bn = b.method_7969() == null ? null : b.method_7969().method_10553();
         if (an != null) {
            an.method_10551("Damage");
         }

         if (bn != null) {
            bn.method_10551("Damage");
         }

         return Objects.equals(an, bn);
      }
   }

   public static int countItems(class_1657 player, class_1799 target) {
      return countItems(player, target, true, false);
   }

   public static int countItems(class_1657 player, class_1799 target, boolean stopUponTargetCount, boolean ignoreNBT) {
      int count = 0;
      int i = 0;

      for(int c = player.method_31548().method_5439(); i < c; ++i) {
         class_1799 stack = player.method_31548().method_5438(i);
         if (areStacksSimilar(target, stack, ignoreNBT)) {
            count += stack.method_7947();
            if (stopUponTargetCount && count >= target.method_7947()) {
               return count;
            }
         }
      }

      return count;
   }

   public static int removeItems(class_1657 player, class_1799 target, int amount, boolean ignoreNBT) {
      int remaining = amount;

      for(int i = 0; i < player.method_31548().method_5439() && remaining > 0; ++i) {
         class_1799 stack = player.method_31548().method_5438(i);
         if (areStacksSimilar(target, stack, ignoreNBT)) {
            int remove = Math.min(remaining, stack.method_7947());
            stack.method_7934(remove);
            remaining -= remove;
         }
      }

      player.method_31548().method_5431();
      return amount - remaining;
   }

   public static int countItems(class_2371<class_1799> inventory, class_1799 target, boolean stopUponTargetCount) {
      int count = 0;
      int i = 0;

      for(int c = inventory.size(); i < c; ++i) {
         class_1799 stack = (class_1799)inventory.get(i);
         if (areStacksSimilar(target, stack)) {
            count += stack.method_7947();
            if (stopUponTargetCount && count >= target.method_7947()) {
               return count;
            }
         }
      }

      return count;
   }
}
