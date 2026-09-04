package mchorse.mappet.api.scripts.user.data;

import net.minecraft.class_2338;
import net.minecraft.class_243;

public class ScriptVector {
   public double x;
   public double y;
   public double z;

   public ScriptVector(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public ScriptVector(class_243 vector) {
      this.x = vector.field_1352;
      this.y = vector.field_1351;
      this.z = vector.field_1350;
   }

   public ScriptVector(class_2338 pos) {
      this.x = (double)pos.method_10263();
      this.y = (double)pos.method_10264();
      this.z = (double)pos.method_10260();
   }

   public String toString() {
      return "ScriptVector(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   public String toArrayString() {
      return "[" + this.x + ", " + this.y + ", " + this.z + "]";
   }

   public ScriptVector add(ScriptVector other) {
      return new ScriptVector(this.x + other.x, this.y + other.y, this.z + other.z);
   }

   public ScriptVector subtract(ScriptVector other) {
      return new ScriptVector(this.x - other.x, this.y - other.y, this.z - other.z);
   }

   public ScriptVector multiply(double scalar) {
      return new ScriptVector(this.x * scalar, this.y * scalar, this.z * scalar);
   }

   public double length() {
      return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public ScriptVector normalize() {
      double length = this.length();
      return new ScriptVector(this.x / length, this.y / length, this.z / length);
   }
}
