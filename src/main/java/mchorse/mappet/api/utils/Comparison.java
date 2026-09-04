package mchorse.mappet.api.utils;

import mchorse.mappet.api.expressions.ExpressionManager;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.utils.EnumUtils;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.MathBuilder;
import mchorse.mclib.math.Variable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class Comparison implements INBTSerializable<class_2487> {
   private static final MathBuilder MATH = new MathBuilder();
   private static final Variable VALUE = new Variable("value", (double)0.0F);
   private static final Variable VALUE2 = new Variable("x", (double)0.0F);
   public ComparisonMode comparison;
   public double value;
   public String expression;
   private IValue compiledValue;

   public Comparison() {
      this.comparison = ComparisonMode.EQUALS;
      this.expression = "";
   }

   public boolean compare(double a) {
      if (this.comparison == ComparisonMode.EXPRESSION) {
         if (this.compiledValue == null) {
            try {
               this.compiledValue = MATH.parse(this.expression);
            } catch (Exception var4) {
               this.compiledValue = ExpressionManager.ZERO;
            }
         }

         VALUE.set(a);
         VALUE2.set(a);
         return this.compiledValue.booleanValue();
      } else {
         return this.comparison.compare(a, this.value);
      }
   }

   public boolean compareString(String a) {
      switch (this.comparison) {
         case EQUALS_TO_STRING -> {
            return a.equals(this.expression);
         }
         case CONTAINS_STRING -> {
            return a.contains(this.expression);
         }
         case REGEXP_STRING -> {
            return a.matches(this.expression);
         }
         default -> {
            return false;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public String stringify(String id) {
      return this.comparison.stringify(id, this.value, this.expression);
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10569("Comparison", this.comparison.ordinal());
      tag.method_10549("Value", this.value);
      tag.method_10582("Expression", this.expression);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.compiledValue = null;
      this.comparison = (ComparisonMode)EnumUtils.getValue(tag.method_10550("Comparison"), ComparisonMode.values(), ComparisonMode.EQUALS);
      this.value = tag.method_10574("Value");
      this.expression = tag.method_10558("Expression");
   }

   static {
      MATH.register(VALUE);
      MATH.register(VALUE2);
   }
}
