package mchorse.mappet.api.vision;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.scripts.user.data.ScriptVector;

public class VisionZone {
   public enum Shape {
      SECTOR, POLYGON
   }

   public Shape shape;
   public double radius;
   public double viewAngle;
   public double viewAngleV;
   public double minHeight;
   public double maxHeight;
   public double yawOffset;
   public double eyeOffset;
   public List<ScriptVector> points = new ArrayList<>();

   public VisionZone(Shape shape) {
      this.shape = shape;
   }

   public boolean contains(double ox, double oz, double entityY, double yaw, double pitch, double x, double y, double z) {
      double heightHalf = (this.maxHeight - this.minHeight) / 2.0;
      double dx = x - ox;
      double dy = y - (entityY + this.eyeOffset);
      double dz = z - oz;

      double ay = Math.toRadians(yaw + this.yawOffset);
      double sinY = Math.sin(ay);
      double cosY = Math.cos(ay);
      double ap = Math.toRadians(pitch);
      double sinP = Math.sin(ap);
      double cosP = Math.cos(ap);

      double lx = dx * cosY + dz * sinY;
      double yz = -dx * sinY + dz * cosY;
      double ly = dy * cosP + yz * sinP;
      double lz = -dy * sinP + yz * cosP;

      if ((this.shape != Shape.SECTOR || this.viewAngleV <= 0.0) && (ly < -heightHalf || ly > heightHalf)) {
         return false;
      }

      if (this.shape == Shape.SECTOR) {
         if (this.viewAngleV > 0.0 && this.viewAngle > 0.0 && this.viewAngle < 360.0) {
            double l2 = lx * lx + ly * ly + lz * lz;

            if (l2 > this.radius * this.radius || lz <= 0.0) {
               return false;
            }

            double halfH = Math.toRadians(this.viewAngle / 2.0);
            double halfV = Math.toRadians(this.viewAngleV / 2.0);

            return Math.abs(lx) <= lz * Math.tan(halfH) && Math.abs(ly) <= lz * Math.tan(halfV);
         }

         if (this.viewAngleV > 0.0 || this.viewAngle >= 360.0 || this.viewAngle <= 0.0) {
            return lx * lx + ly * ly + lz * lz <= this.radius * this.radius;
         }

         double dist2 = lx * lx + lz * lz;

         if (dist2 > this.radius * this.radius) {
            return false;
         }

         if (dist2 == 0.0) {
            return true;
         }

         double half = Math.toRadians(this.viewAngle / 2.0);
         double cos = lz / Math.sqrt(dist2);

         return cos >= Math.cos(half);
      } else {
         return this.pointInPolygon(lx, lz);
      }
   }

   private boolean pointInPolygon(double x, double z) {
      int n = this.points.size();
      if (n < 3) {
         return false;
      }

      boolean inside = false;

      for (int i = 0, j = n - 1; i < n; j = i++) {
         ScriptVector a = this.points.get(i);
         ScriptVector b = this.points.get(j);
         boolean intersect = (a.z > z) != (b.z > z) && x < (b.x - a.x) * (z - a.z) / (b.z - a.z) + a.x;

         if (intersect) {
            inside = !inside;
         }
      }

      return inside;
   }

   public void write(ByteBuf buf) {
      buf.writeInt(this.shape.ordinal());
      buf.writeDouble(this.radius);
      buf.writeDouble(this.viewAngle);
      buf.writeDouble(this.viewAngleV);
      buf.writeDouble(this.minHeight);
      buf.writeDouble(this.maxHeight);
      buf.writeDouble(this.yawOffset);
      buf.writeDouble(this.eyeOffset);
      buf.writeInt(this.points.size());

      for (ScriptVector point : this.points) {
         buf.writeDouble(point.x);
         buf.writeDouble(point.z);
      }
   }

   public static VisionZone read(ByteBuf buf) {
      VisionZone zone = new VisionZone(Shape.values()[buf.readInt()]);
      zone.radius = buf.readDouble();
      zone.viewAngle = buf.readDouble();
      zone.viewAngleV = buf.readDouble();
      zone.minHeight = buf.readDouble();
      zone.maxHeight = buf.readDouble();
      zone.yawOffset = buf.readDouble();
      zone.eyeOffset = buf.readDouble();

      int size = buf.readInt();

      for (int i = 0; i < size; i++) {
         zone.points.add(new ScriptVector(buf.readDouble(), 0, buf.readDouble()));
      }

      return zone;
   }
}