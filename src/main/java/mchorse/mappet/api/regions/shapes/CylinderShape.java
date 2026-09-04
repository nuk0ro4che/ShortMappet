package mchorse.mappet.api.regions.shapes;

public class CylinderShape extends SphereShape {
   public CylinderShape() {
   }

   public CylinderShape(double horizontal, double vertical) {
      super(horizontal, vertical);
   }

   public boolean isInside(double x, double y, double z) {
      double dx = x - this.pos.x;
      double dy = y - this.pos.y;
      double dz = z - this.pos.z;
      boolean isXZ = Math.sqrt(dx * dx + dz * dz) <= this.horizontal;
      boolean isY = Math.abs(dy) < this.vertical;
      return isXZ && isY;
   }

   public String getType() {
      return "cylinder";
   }
}
