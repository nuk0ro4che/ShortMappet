package mchorse.mappet.api.utils;

import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.math.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public enum ComparisonMode {
   LESS(Operation.LESS),
   LESS_THAN(Operation.LESS_THAN),
   EQUALS(Operation.EQUALS),
   GREATER_THAN(Operation.GREATER_THAN),
   GREATER(Operation.GREATER),
   IS_TRUE((Operation)null) {
      public boolean compare(double a, double b) {
         return Operation.isTrue(a);
      }

      @Environment(EnvType.CLIENT)
      public String stringify(String a, double b, String expression) {
         return a + " == true";
      }

      @Environment(EnvType.CLIENT)
      public IKey stringify() {
         return IKey.lang("mappet.gui.conditions.comparisons.is_true");
      }
   },
   IS_FALSE((Operation)null) {
      public boolean compare(double a, double b) {
         return !Operation.isTrue(a);
      }

      @Environment(EnvType.CLIENT)
      public String stringify(String a, double b, String expression) {
         return a + " == false";
      }

      @Environment(EnvType.CLIENT)
      public IKey stringify() {
         return IKey.lang("mappet.gui.conditions.comparisons.is_false");
      }
   },
   EXPRESSION((Operation)null) {
      public String stringify(String a, double b, String expression) {
         return expression;
      }

      public IKey stringify() {
         return IKey.lang("mappet.gui.conditions.expression");
      }
   },
   EQUALS_TO_STRING((Operation)null, true) {
      @Environment(EnvType.CLIENT)
      public String stringify(String a, double b, String expression) {
         return a + " == \"" + expression + "\"";
      }

      @Environment(EnvType.CLIENT)
      public IKey stringify() {
         return IKey.str("\"\"");
      }
   },
   CONTAINS_STRING((Operation)null, true) {
      @Environment(EnvType.CLIENT)
      public String stringify(String a, double b, String expression) {
         return a + " " + String.valueOf(IKey.lang("mappet.gui.conditions.comparisons.contains_string")) + " \"" + expression + "\"";
      }

      @Environment(EnvType.CLIENT)
      public IKey stringify() {
         return IKey.comp(new IKey[]{IKey.lang("mappet.gui.conditions.comparisons.contains_string"), IKey.str(" \"\"")});
      }
   },
   REGEXP_STRING((Operation)null, true) {
      @Environment(EnvType.CLIENT)
      public String stringify(String a, double b, String expression) {
         return a + " match /" + expression + "/";
      }

      @Environment(EnvType.CLIENT)
      public IKey stringify() {
         return IKey.lang("mappet.gui.conditions.comparisons.regexp");
      }
   };

   public final Operation operation;
   public final boolean isString;

   private ComparisonMode(Operation operation) {
      this(operation, false);
   }

   private ComparisonMode(Operation operation, boolean isString) {
      this.operation = operation;
      this.isString = isString;
   }

   public boolean compare(double a, double b) {
      return this.operation.calculate(a, b) == (double)1.0F;
   }

   @Environment(EnvType.CLIENT)
   public String stringify(String a, double b, String expression) {
      return a + " " + this.operation.sign + " " + b;
   }

   @Environment(EnvType.CLIENT)
   public IKey stringify() {
      return IKey.str(this.operation.sign);
   }
   private static ComparisonMode[] $values() {
      return new ComparisonMode[]{LESS, LESS_THAN, EQUALS, GREATER_THAN, GREATER, IS_TRUE, IS_FALSE, EXPRESSION, EQUALS_TO_STRING, CONTAINS_STRING, REGEXP_STRING};
   }
}
