package mchorse.mappet.api.scripts.user.sounds;


public interface IScriptManagedSound {
   String getId();

   String getName();

   void setPosition(double x, double y, double z);

   void setVolume(float volume);

   double getTimeCode();

   void setTimeCode(double seconds);

   void stop();
}
