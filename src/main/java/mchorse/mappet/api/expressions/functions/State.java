package mchorse.mappet.api.expressions.functions;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.states.States;
import mchorse.mappet.compat.CommandBase;
import mchorse.mappet.utils.EntityUtils;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.Function;

public class State extends Function {
   public static States getState(String target) {
      States states = null;
      if (target.equals("~")) {
         states = Mappet.states;
      } else {
         try {
            states = EntityUtils.getStates(CommandBase.getEntity(Mappet.expressions.getServer(), Mappet.expressions.getServer(), target));
         } catch (Exception var3) {
         }
      }

      return states;
   }

   public State(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public Object getValue() {
      String target = this.args.length > 1 ? this.getArg(1).stringValue() : "~";
      States states = getState(target);
      return states == null ? null : states.values.get(this.getArg(0).stringValue());
   }

   public int getRequiredArguments() {
      return 1;
   }

   public IValue get() {
      if (this.isNumber()) {
         this.result.set(this.doubleValue());
      } else {
         this.result.set(this.stringValue());
      }

      return this.result;
   }

   public boolean isNumber() {
      return !(this.getValue() instanceof String);
   }

   public double doubleValue() {
      Object value = this.getValue();
      return value instanceof Number ? ((Number)value).doubleValue() : (double)0.0F;
   }

   public boolean booleanValue() {
      return this.getValue() != null;
   }

   public String stringValue() {
      Object value = this.getValue();
      return value instanceof String ? (String)value : "";
   }
}
