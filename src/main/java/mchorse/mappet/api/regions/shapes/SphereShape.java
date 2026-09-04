package mchorse.mappet.api.regions.shapes;

import net.minecraft.class_2487;

public class SphereShape extends AbstractShape {
   public double horizontal = (double)1.0F;
   public double vertical = (double)1.0F;

   public SphereShape() {
   }

   public SphereShape(double horizontal, double vertical) {
      this.horizontal = horizontal;
      this.vertical = vertical;
   }

   public void copyFrom(AbstractShape shape) {
      super.copyFrom(shape);
      if (shape instanceof BoxShape) {
         this.horizontal = ((BoxShape)shape).size.x;
         this.vertical = ((BoxShape)shape).size.y;
      } else if (shape instanceof SphereShape) {
         this.horizontal = ((SphereShape)shape).horizontal;
         this.vertical = ((SphereShape)shape).vertical;
      }

   }

   public boolean isInside(double x, double y, double z) {
      double dx = x - this.pos.x;
      double dy = y - this.pos.y;
      double dz = z - this.pos.z;
      double rx = dx / this.horizontal;
      double ry = dy / this.vertical;
      double rz = dz / this.horizontal;
      return rx * rx + ry * ry + rz * rz <= (double)1.0F;
   }

   public String getType() {
      return "sphere";
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10549("Horizontal", this.horizontal);
      tag.method_10549("Vertical", this.vertical);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Horizontal")) {
         this.horizontal = tag.method_10574("Horizontal");
      }

      if (tag.method_10545("Vertical")) {
         this.vertical = tag.method_10574("Vertical");
      }

   }
}
