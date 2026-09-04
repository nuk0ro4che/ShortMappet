package mchorse.mappet.api.regions.shapes;

import javax.vecmath.Vector3d;
import net.minecraft.class_2487;

public class BoxShape extends AbstractShape {
   public Vector3d size = new Vector3d((double)1.0F, (double)1.0F, (double)1.0F);

   public void copyFrom(AbstractShape shape) {
      super.copyFrom(shape);
      if (shape instanceof BoxShape) {
         this.size.set(((BoxShape)shape).size);
      } else if (shape instanceof SphereShape) {
         double h = ((SphereShape)shape).horizontal;
         double v = ((SphereShape)shape).vertical;
         this.size.set(h, v, h);
      }

   }

   public boolean isInside(double x, double y, double z) {
      double dx = x - this.pos.x;
      double dy = y - this.pos.y;
      double dz = z - this.pos.z;
      return Math.abs(dx) < this.size.x && Math.abs(dy) < this.size.y && Math.abs(dz) < this.size.z;
   }

   public String getType() {
      return "box";
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10549("SizeX", this.size.x);
      tag.method_10549("SizeY", this.size.y);
      tag.method_10549("SizeZ", this.size.z);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("SizeX") && tag.method_10545("SizeY") && tag.method_10545("SizeZ")) {
         this.size.x = tag.method_10574("SizeX");
         this.size.y = tag.method_10574("SizeY");
         this.size.z = tag.method_10574("SizeZ");
      }

   }
}
