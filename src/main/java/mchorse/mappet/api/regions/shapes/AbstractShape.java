package mchorse.mappet.api.regions.shapes;

import javax.vecmath.Vector3d;
import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2487;

public abstract class AbstractShape implements INBTSerializable<class_2487> {
   public Vector3d pos = new Vector3d();

   public static AbstractShape fromString(String string) {
      if (string.equals("box")) {
         return new BoxShape();
      } else if (string.equals("sphere")) {
         return new SphereShape();
      } else {
         return string.equals("cylinder") ? new CylinderShape() : null;
      }
   }

   public void copyFrom(AbstractShape shape) {
      this.pos.set(shape.pos);
   }

   public boolean isEntityInside(class_1297 entity, class_2338 tile) {
      return this.pos == null ? false : this.isEntityInside(entity.method_23317(), entity.method_23318() + (double)(entity.method_17682() / 2.0F), entity.method_23321(), tile);
   }

   public boolean isEntityInside(double x, double y, double z, class_2338 tile) {
      return this.pos == null ? false : this.isInside(x - (double)tile.method_10263() - (double)0.5F, y - (double)tile.method_10264() - (double)0.5F, z - (double)tile.method_10260() - (double)0.5F);
   }

   public abstract String getType();

   public abstract boolean isInside(double var1, double var3, double var5);

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10549("PosX", this.pos.x);
      tag.method_10549("PosY", this.pos.y);
      tag.method_10549("PosZ", this.pos.z);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("PosX") && tag.method_10545("PosY") && tag.method_10545("PosZ")) {
         this.pos = new Vector3d(tag.method_10574("PosX"), tag.method_10574("PosY"), tag.method_10574("PosZ"));
      }

   }
}
