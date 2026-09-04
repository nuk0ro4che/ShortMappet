package mchorse.mappet.api.expressions.functions.inventory;

import mchorse.mclib.math.IValue;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_7923;

public class InventoryHas extends InventoryFunction {
   public static boolean compareItemById(String id, class_1799 stack) {
      return class_7923.field_41178.method_10221(stack.method_7909()).toString().equals(id);
   }

   public static boolean compareInventory(String id, class_2371<class_1799> inventory) {
      for(class_1799 stack : inventory) {
         if (!stack.method_7960() && compareItemById(id, stack)) {
            return true;
         }
      }

      return false;
   }

   public InventoryHas(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   protected boolean isTrue(String id, class_1657 player) {
      return compareInventory(id, player.method_31548().field_7547);
   }
}
