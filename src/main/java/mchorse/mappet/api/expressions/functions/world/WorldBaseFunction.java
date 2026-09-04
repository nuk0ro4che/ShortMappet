package mchorse.mappet.api.expressions.functions.world;

import mchorse.mappet.Mappet;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.NNFunction;
import net.minecraft.class_1937;

public abstract class WorldBaseFunction extends NNFunction {
   public WorldBaseFunction(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   protected class_1937 getWorld() {
      class_1937 world = Mappet.expressions.getWorld();
      if (this.args.length > 0) {
         int dimension = (int)this.getArg(0).doubleValue();
         if (Mappet.server != null) {
            world = Mappet.server.method_3847(dimension == -1 ? class_1937.field_25180 : (dimension == 1 ? class_1937.field_25181 : class_1937.field_25179));
         }
      }

      return world;
   }
}
