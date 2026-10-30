package mchorse.mappet.utils;

import net.minecraft.class_1799;
import net.minecraft.class_1839;

public final class ItemUtils {
   private ItemUtils() {
   }

   public static boolean isFood(class_1799 stack) {
      return stack != null && !stack.method_7960() && stack.method_7909().method_19263();
   }

   public static boolean isDrinkable(class_1799 stack) {
      return stack != null && !stack.method_7960() && stack.method_7909().method_7853(stack) == class_1839.field_8946;
   }
}
