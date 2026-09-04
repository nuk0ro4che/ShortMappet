package mchorse.mappet.api.expressions.functions.world;

import mchorse.mclib.math.IValue;
import net.minecraft.class_1937;

public class WorldTotalTime extends WorldBaseFunction {
   public WorldTotalTime(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public double doubleValue() {
      class_1937 world = this.getWorld();
      return world == null ? (double)0.0F : (double)world.method_8510();
   }
}
