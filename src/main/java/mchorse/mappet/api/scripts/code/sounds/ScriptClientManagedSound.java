package mchorse.mappet.api.scripts.code.sounds;

import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.sounds.IScriptManagedSound;
import mchorse.mappet.client.sounds.ClientManagedSoundManager;
import mchorse.mappet.network.common.scripts.PacketManagedSound;


public class ScriptClientManagedSound implements IScriptManagedSound {
   private final String id;
   private String name;
   private double x;
   private double y;
   private double z;
   private float volume;

   public ScriptClientManagedSound(String id, String name, double x, double y, double z, float volume) {
      this.id = id;
      this.name = name == null ? "" : name;
      this.x = x;
      this.y = y;
      this.z = z;
      this.volume = Math.max(0.0F, volume);
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      String live = ClientManagedSoundManager.getName(this.id);
      return live.isEmpty() ? this.name : live;
   }

   public ScriptVector getPosition() {
      if (!ClientManagedSoundManager.has(this.id)) {
         return new ScriptVector(this.x, this.y, this.z);
      }

      return new ScriptVector(ClientManagedSoundManager.getX(this.id), ClientManagedSoundManager.getY(this.id), ClientManagedSoundManager.getZ(this.id));
   }

   public float getVolume() {
      return ClientManagedSoundManager.has(this.id) ? ClientManagedSoundManager.getVolume(this.id) : this.volume;
   }

   public void setPosition(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      ClientManagedSoundManager.handle(PacketManagedSound.update(this.id, x, y, z, this.volume));
   }

   public void setVolume(float volume) {
      this.volume = Math.max(0.0F, volume);
      ClientManagedSoundManager.handle(PacketManagedSound.update(this.id, this.x, this.y, this.z, this.volume));
   }

   public double getTimeCode() {
      return ClientManagedSoundManager.getTimeCode(this.id);
   }

   public void setTimeCode(double seconds) {
      ClientManagedSoundManager.handle(PacketManagedSound.setTimeCode(this.id, Math.max(0.0D, seconds)));
   }

   public boolean isPlaying() {
      return ClientManagedSoundManager.has(this.id);
   }

   public boolean isPaused() {
      return ClientManagedSoundManager.has(this.id) && ClientManagedSoundManager.isPaused(this.id);
   }

   public void pause() {
      ClientManagedSoundManager.handle(PacketManagedSound.pause(this.id));
   }

   public void resume() {
      ClientManagedSoundManager.handle(PacketManagedSound.resume(this.id));
   }

   public void stop() {
      ClientManagedSoundManager.handle(PacketManagedSound.stop(this.id));
   }
}
