package mchorse.mappet.api.events.nodes;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.events.EventContext;
import mchorse.mclib.math.IValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class SwitchNode extends EventBaseNode {
   public String expression = "";

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return this.expression;
   }

   public int execute(EventContext context) {
      IValue value = Mappet.expressions.set(context.data).parse(this.expression, (IValue)null);
      if (value != null && value.isNumber()) {
         int result = 1 + (int)value.get().doubleValue();
         context.log("Expression \"" + this.expression + "\" is going to switch to its " + result + " execution branch...");
         return result;
      } else {
         context.log("Switching \"" + this.expression + "\" could not be executed!");
         return this.booleanToExecutionCode(false);
      }
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      if (!this.expression.isEmpty()) {
         tag.method_10582("Expression", this.expression);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Expression")) {
         this.expression = tag.method_10558("Expression");
      }

   }
}
