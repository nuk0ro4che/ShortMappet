package mchorse.mappet.api.expressions.functions.inventory;

import mchorse.mclib.math.IValue;
import net.minecraft.class_1304;
import net.minecraft.class_1657;

public class InventoryHolds extends InventoryFunction {
   public InventoryHolds(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   protected boolean isTrue(String id, class_1657 player) {
      return InventoryHas.compareItemById(id, player.method_6118(class_1304.field_6173)) || InventoryHas.compareItemById(id, player.method_6118(class_1304.field_6171));
   }
}
