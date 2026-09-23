package mchorse.mappet.api.scripts.code.entities;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.entities.IScriptVisionZone;
import mchorse.mappet.api.vision.VisionZone;

public class ScriptVisionZone implements IScriptVisionZone {
   private VisionZone zone;

   public ScriptVisionZone(VisionZone zone) {
      this.zone = zone;
   }

   @Override
   public String getShape() {
      return this.zone.shape == VisionZone.Shape.SECTOR ? "sector" : "polygon";
   }

   @Override
   public double getRadius() {
      return this.zone.radius;
   }

   @Override
   public double getViewAngle() {
      return this.zone.viewAngle;
   }

   @Override
   public double getViewAngleV() {
      return this.zone.viewAngleV;
   }

   @Override
   public double getMinHeight() {
      return this.zone.minHeight;
   }

   @Override
   public double getMaxHeight() {
      return this.zone.maxHeight;
   }

   @Override
   public double getYawOffset() {
      return this.zone.yawOffset;
   }

   @Override
   public List<ScriptVector> getPoints() {
      if (this.zone.shape != VisionZone.Shape.POLYGON) {
         return new ArrayList<ScriptVector>();
      }

      List<ScriptVector> points = new ArrayList<ScriptVector>();

      for (ScriptVector point : this.zone.points) {
         points.add(new ScriptVector(point.x, 0, point.z));
      }

      return points;
   }
}