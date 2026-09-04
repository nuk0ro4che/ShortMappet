package mchorse.mappet.api.expressions.functions.inventory;

import mchorse.mclib.math.IValue;
import net.minecraft.class_1657;

public class InventoryArmor extends InventoryFunction {
   public InventoryArmor(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   protected boolean isTrue(String id, class_1657 player) {
      return InventoryHas.compareInventory(id, player.method_31548().field_7548);
   }
}
