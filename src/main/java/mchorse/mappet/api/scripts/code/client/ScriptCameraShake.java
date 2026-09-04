package mchorse.mappet.api.scripts.code.client;

import mchorse.mappet.api.scripts.user.client.ICameraShake;
import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.camera.CameraShake;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketCameraShake;
import net.minecraft.class_1657;
import net.minecraft.class_3222;

public class ScriptCameraShake implements ICameraShake {
   private final class_1657 player;
   private final CameraShake shake;

   public ScriptCameraShake(class_1657 player) {
      this.player = player;
      this.shake = Character.get(player).getCameraShake();
   }

   public void start(int ticks, double intensity) {
      this.start(ticks, intensity, 1.0D);
   }

   public void start(int ticks, double intensity, double frequency) {
      this.start(ticks, intensity, intensity, 0.0D, frequency);
   }

   public void start(int ticks, double pitch, double yaw, double roll, double frequency) {
      this.shake.start(ticks, pitch, yaw, roll, frequency);
      this.sync();
   }

   public void stop() {
      this.shake.stop();
      this.sync();
   }

   public boolean isActive() {
      return this.shake.isActive();
   }

   public void setIntensity(double intensity) {
      this.setIntensity(intensity, intensity, 0.0D);
   }

   public void setIntensity(double pitch, double yaw, double roll) {
      this.shake.setIntensity(pitch, yaw, roll);
      this.sync();
   }

   public ScriptVector getIntensity() {
      return this.shake.getIntensity();
   }

   public void setFrequency(double frequency) {
      this.shake.setFrequency(frequency);
      this.sync();
   }

   public double getFrequency() {
      return this.shake.getFrequency();
   }

   public void setDuration(int ticks) {
      this.shake.setDuration(ticks);
      this.sync();
   }

   public int getDuration() {
      return this.shake.getDuration();
   }

   private void sync() {
      ScriptVector intensity = this.shake.getIntensity();
      if (this.player instanceof class_3222) {
         Dispatcher.sendTo(new PacketCameraShake(this.shake.isActive(), this.shake.getDuration(), intensity.x, intensity.y, intensity.z, this.shake.getFrequency()), (class_3222)this.player);
      }
   }
}
