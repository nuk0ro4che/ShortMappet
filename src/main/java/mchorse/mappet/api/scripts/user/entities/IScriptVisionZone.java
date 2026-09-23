package mchorse.mappet.api.scripts.user.entities;

import java.util.List;
import mchorse.mappet.api.scripts.user.data.ScriptVector;

public interface IScriptVisionZone {
   String getShape();

   double getRadius();

   double getViewAngle();

   double getViewAngleV();

   double getMinHeight();

   double getMaxHeight();

   double getYawOffset();

   List<ScriptVector> getPoints();
}