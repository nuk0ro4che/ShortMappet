package mchorse.mappet.api.events.nodes;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.events.EventContext;
import mchorse.mclib.math.IValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class ConditionNode extends EventBaseNode {
   public Checker condition = new Checker();

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return this.condition.mode == Checker.Mode.CONDITION ? "" : this.condition.expression;
   }

   public int execute(EventContext context) {
      if (this.condition.mode == Checker.Mode.CONDITION) {
         boolean result = this.condition.condition.execute(context.data);
         context.log("The result of condition is " + (result ? "true" : "false"));
         return this.booleanToExecutionCode(result);
      } else {
         String expression = this.condition.expression;
         IValue value = Mappet.expressions.set(context.data).parse(expression, (IValue)null);
         if (value != null) {
            boolean result = value.booleanValue();
            context.log("The result \"" + expression + "\" is " + (result ? "true" : "false"));
            return this.booleanToExecutionCode(result);
         } else {
            context.log("Condition \"" + expression + "\" could not be executed!");
            return this.booleanToExecutionCode(false);
         }
      }
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10566("Condition", this.condition.serializeNBT());
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Condition")) {
         this.condition.deserializeNBT(tag.method_10580("Condition"));
      }

   }
}
