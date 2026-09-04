package mchorse.mappet.utils;

import java.util.Set;
import mchorse.mappet.common.ScriptedItemProps;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2489;
import net.minecraft.class_2497;
import net.minecraft.class_2499;
import net.minecraft.class_2514;
import net.minecraft.class_2519;
import net.minecraft.class_2520;

public class NBTUtils {
   public static class_2338 blockPosFrom(class_2520 base) {
      if (base instanceof class_2499 list && ((class_2499)base).size() >= 3) {
         class_2520 x = list.method_10534(0);
         class_2520 y = list.method_10534(1);
         class_2520 z = list.method_10534(2);
         if (x instanceof class_2514 && y instanceof class_2514 && z instanceof class_2514) {
            return new class_2338(((class_2514)x).method_10701(), ((class_2514)y).method_10701(), ((class_2514)z).method_10701());
         }
      }

      return null;
   }

   public static class_2520 blockPosTo(class_2338 pos) {
      class_2499 list = new class_2499();
      list.add(class_2497.method_23247(pos.method_10263()));
      list.add(class_2497.method_23247(pos.method_10264()));
      list.add(class_2497.method_23247(pos.method_10260()));
      return list;
   }

   public static class_243 vec3dFrom(class_2520 base) {
      if (base instanceof class_2499 list && ((class_2499)base).size() >= 3) {
         class_2520 x = list.method_10534(0);
         class_2520 y = list.method_10534(1);
         class_2520 z = list.method_10534(2);
         if (x instanceof class_2514 && y instanceof class_2514 && z instanceof class_2514) {
            return new class_243(((class_2514)x).method_10697(), ((class_2514)y).method_10697(), ((class_2514)z).method_10697());
         }
      }

      return null;
   }

   public static class_2520 vec3dTo(class_243 vec) {
      class_2499 list = new class_2499();
      list.add(class_2489.method_23241(vec.field_1352));
      list.add(class_2489.method_23241(vec.field_1351));
      list.add(class_2489.method_23241(vec.field_1350));
      return list;
   }

   public static String[] getStringArray(class_2499 list) {
      String[] array = new String[list.size()];

      for(int i = 0; i < list.size(); ++i) {
         array[i] = list.method_10608(i);
      }

      return array;
   }

   public static void writeStringList(class_2499 list, Set<String> set) {
      set.stream().forEach((string) -> list.add(class_2519.method_23256(string)));
   }

   public static boolean saveScriptedItemProps(class_1799 stack, class_2487 tag) {
      if (stack.method_7909().equals(class_1802.field_8162)) {
         return false;
      } else {
         if (!stack.method_7985()) {
            stack.method_7980(new class_2487());
         }

         if (stack.method_7985()) {
            stack.method_7969().method_10566("ScriptedItem", tag);
            return true;
         } else {
            return false;
         }
      }
   }

   public static ScriptedItemProps getScriptedItemProps(class_1799 stack) {
      if (stack.method_7909().equals(class_1802.field_8162)) {
         return null;
      } else {
         if (stack.method_7985()) {
            class_2487 tag = stack.method_7969();
            if (tag.method_10545("ScriptedItem")) {
               return new ScriptedItemProps(tag.method_10562("ScriptedItem"));
            }
         }

         return new ScriptedItemProps();
      }
   }

   public static void setScriptedItemProps(class_1799 itemStack, ScriptedItemProps props) {
      class_2487 compound = itemStack.method_7969();
      if (compound == null) {
         compound = new class_2487();
         itemStack.method_7980(compound);
      }

      compound.method_10566("ScriptedItem", props.toNBT());
   }
}
