package mchorse.mappet.api.conditions;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.expressions.ExpressionManager;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.utils.EnumUtils;
import mchorse.mclib.math.IValue;
import net.minecraft.class_2487;
import net.minecraft.class_2519;
import net.minecraft.class_2520;

public class Checker implements INBTSerializable<class_2520> {
   public String expression;
   public Condition condition;
   public Mode mode;
   private IValue value;
   private boolean defaultValue;

   public Checker() {
      this(false);
   }

   public Checker(boolean defaultValue) {
      this.expression = "";
      this.mode = Checker.Mode.CONDITION;
      this.defaultValue = defaultValue;
      this.condition = new Condition(defaultValue);
   }

   public boolean check(DataContext data) {
      if (this.isEmpty()) {
         return this.defaultValue;
      } else if (this.mode == Checker.Mode.CONDITION) {
         return this.condition.execute(data);
      } else {
         if (this.value == null) {
            this.value = Mappet.expressions.parse(this.expression, this.defaultValue ? ExpressionManager.ONE : ExpressionManager.ZERO);
         }

         Mappet.expressions.set(data);
         return this.value.booleanValue();
      }
   }

   public boolean isEmpty() {
      return this.mode == Checker.Mode.CONDITION ? this.condition.blocks.isEmpty() : this.expression.isEmpty();
   }

   public class_2487 toNBT() {
      return (class_2487)this.serializeNBT();
   }

   public class_2520 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10569("Mode", this.mode.ordinal());
      if (!this.expression.isEmpty()) {
         tag.method_10582("Expression", this.expression);
      }

      tag.method_10566("Condition", this.condition.serializeNBT());
      return tag;
   }

   public void deserializeNBT(class_2520 base) {
      if (base instanceof class_2487 tag) {
         this.value = null;
         if (tag.method_10573("Mode", 99)) {
            this.mode = (Mode)EnumUtils.getValue(tag.method_10550("Mode"), Checker.Mode.values(), Checker.Mode.CONDITION);
         }

         if (tag.method_10545("Expression")) {
            this.expression = tag.method_10558("Expression");
         }

         if (tag.method_10545("Condition")) {
            this.condition.deserializeNBT(tag.method_10562("Condition"));
         }
      } else if (base instanceof class_2519) {
         this.expression = ((class_2519)base).method_10714();
         this.mode = Checker.Mode.EXPRESSION;
      }

   }

   public String toString() {
      String var10000 = String.valueOf(this.mode);
      return "Checker[mode:" + var10000 + ",condition:" + this.condition.toString() + "]";
   }

   public static enum Mode {
      EXPRESSION,
      CONDITION;
      private static Mode[] $values() {
         return new Mode[]{EXPRESSION, CONDITION};
      }
   }
}
