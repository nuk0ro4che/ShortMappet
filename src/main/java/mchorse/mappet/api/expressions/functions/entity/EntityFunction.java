package mchorse.mappet.api.expressions.functions.entity;

import mchorse.mappet.Mappet;
import mchorse.mappet.compat.CommandBase;
import mchorse.mappet.utils.EntityUtils;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;
import net.minecraft.class_1297;

public class EntityFunction extends SNFunction {
   public EntityFunction(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 2;
   }

   public double doubleValue() {
      try {
         String property = this.getArg(0).stringValue();
         String target = this.getArg(1).stringValue();
         class_1297 entity = CommandBase.getEntity(Mappet.expressions.getServer(), Mappet.expressions.getServer(), target);
         return EntityUtils.getProperty(entity, property);
      } catch (Exception var4) {
         return (double)0.0F;
      }
   }
}
