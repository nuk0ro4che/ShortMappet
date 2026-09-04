package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.expressions.ExpressionManager;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mclib.math.IValue;
import net.minecraft.class_2487;

public class ExpressionConditionBlock extends AbstractConditionBlock {
   public String expression = "";
   private IValue compiled;

   protected boolean evaluateBlock(DataContext context) {
      if (this.compiled == null) {
         this.compiled = Mappet.expressions.parse(this.expression, ExpressionManager.ZERO);
      }

      return this.compiled.booleanValue();
   }

   public String stringify() {
      return this.expression;
   }

   protected void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("Expression", this.expression);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.expression = tag.method_10558("Expression");
      this.compiled = null;
   }
}
