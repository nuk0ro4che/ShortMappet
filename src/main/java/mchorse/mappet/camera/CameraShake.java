package mchorse.mappet.camera;

import mchorse.mappet.api.scripts.user.data.ScriptVector;

public class CameraShake {
   private boolean active;
   private double pitch;
   private double yaw;
   private double roll;
   private double frequency = 1.0D;
   private long endsAt;

   public void start(int ticks, double pitch, double yaw, double roll, double frequency) {
      this.active = true;
      this.pitch = pitch;
      this.yaw = yaw;
      this.roll = roll;
      this.frequency = Math.max(0.0D, frequency);
      this.setDuration(ticks);
   }

   public void stop() {
      this.active = false;
      this.endsAt = 0L;
   }

   public boolean isActive() {
      if (this.active && this.endsAt > 0L && System.currentTimeMillis() >= this.endsAt) {
         this.stop();
      }

      return this.active;
   }

   public void setIntensity(double pitch, double yaw, double roll) {
      this.pitch = pitch;
      this.yaw = yaw;
      this.roll = roll;
   }

   public ScriptVector getIntensity() {
      return new ScriptVector(this.pitch, this.yaw, this.roll);
   }

   public void setFrequency(double frequency) {
      this.frequency = Math.max(0.0D, frequency);
   }

   public double getFrequency() {
      return this.frequency;
   }

   public void setDuration(int ticks) {
      this.endsAt = ticks <= 0 ? 0L : System.currentTimeMillis() + (long)ticks * 50L;
   }

   public int getDuration() {
      if (!this.isActive() || this.endsAt == 0L) {
         return 0;
      }

      return Math.max(1, (int)Math.ceil((double)(this.endsAt - System.currentTimeMillis()) / 50.0D));
   }
}
