package mchorse.mappet.api.scripts.user.render;

import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.metamorph.api.morphs.AbstractMorph;

public interface IScriptHand {
   void resetAll();
   void setRotations(double x, double y, double z);
   void setRotations(ScriptVector rotations);
   ScriptVector getRotations();
   void setPosition(double x, double y, double z);
   void setPosition(ScriptVector position);
   ScriptVector getPosition();
   void moveTo(String interpolation, int ticks, double x, double y, double z);
   void moveTo(String interpolation, int ticks, ScriptVector position);
   void rotateTo(String interpolation, int ticks, double pitch, double yaw, double roll);
   void rotateTo(String interpolation, int ticks, ScriptVector rotations);
   boolean isRender();
   boolean isRender(boolean item);
   void setRender(boolean render);
   void setRender(boolean render, boolean item);
   void setMorph(AbstractMorph morph);
   void playAnimation(String animation);
}
