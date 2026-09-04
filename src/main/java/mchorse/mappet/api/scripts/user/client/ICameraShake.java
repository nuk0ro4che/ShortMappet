package mchorse.mappet.api.scripts.user.client;

import mchorse.mappet.api.scripts.user.data.ScriptVector;

public interface ICameraShake {
   void start(int ticks, double intensity);

   void start(int ticks, double intensity, double frequency);

   void start(int ticks, double pitch, double yaw, double roll, double frequency);

   void stop();

   boolean isActive();

   void setIntensity(double intensity);

   void setIntensity(double pitch, double yaw, double roll);

   ScriptVector getIntensity();

   void setFrequency(double frequency);

   double getFrequency();

   void setDuration(int ticks);

   int getDuration();
}
