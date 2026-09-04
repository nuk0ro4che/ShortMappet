package mchorse.mappet.api.expressions.functions.world;

import mchorse.mclib.math.IValue;
import net.minecraft.class_1937;

public class WorldIsDay extends WorldBaseFunction {
   public WorldIsDay(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public double doubleValue() {
      class_1937 world = this.getWorld();
      if (world == null) {
         return (double)0.0F;
      } else {
         return world.method_8532() % 24000L < 12000L ? (double)1.0F : (double)0.0F;
      }
   }
}
