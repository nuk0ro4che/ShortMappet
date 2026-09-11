package mchorse.mappet.api.scripts.user.sounds;

import mchorse.mappet.api.scripts.user.data.ScriptVector;

public interface IScriptManagedSound {
   String getId();

   String getName();

   void setPosition(double x, double y, double z);

   ScriptVector getPosition();

   void setVolume(float volume);

   float getVolume();

   boolean isPlaying();

   boolean isPaused();

   void pause();

   void resume();

   double getTimeCode();

   void setTimeCode(double seconds);

   void stop();
}
