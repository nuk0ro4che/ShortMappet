package mchorse.mappet.api.scripts.code.sounds;

import mchorse.mappet.api.scripts.user.sounds.IScriptManagedSound;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import net.minecraft.class_3222;


public class ScriptManagedSound implements IScriptManagedSound {
   private final class_3222 player;
   private final String id;

   public ScriptManagedSound(class_3222 player, String id) {
      this.player = player;
      this.id = id;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      ManagedSoundRegistry.State state = this.state();
      return state == null ? "" : state.name;
   }

   public void setPosition(double x, double y, double z) {
      ManagedSoundRegistry.State state = this.state();
      if (state != null) {
         ManagedSoundRegistry.update(this.player, this.id, x, y, z, state.volume);
         Dispatcher.sendTo(PacketManagedSound.update(this.id, x, y, z, state.volume), this.player);
      }
   }

   public void setVolume(float volume) {
      ManagedSoundRegistry.State state = this.state();
      if (state != null) {
         float liveVolume = Math.max(0.0F, volume);
         ManagedSoundRegistry.update(this.player, this.id, state.x, state.y, state.z, liveVolume);
         
         Dispatcher.sendTo(PacketManagedSound.update(this.id, state.x, state.y, state.z, liveVolume), this.player);
      }
   }

   public double getTimeCode() {
      if (this.state() == null) {
         return 0.0D;
      }

      Dispatcher.sendTo(PacketManagedSound.requestTimeCode(this.id), this.player);
      return ManagedSoundRegistry.getTimeCode(this.player, this.id);
   }

   public void setTimeCode(double seconds) {
      if (this.state() != null) {
         double timeCode = Math.max(0.0D, seconds);
         ManagedSoundRegistry.setTimeCode(this.player, this.id, timeCode);
         Dispatcher.sendTo(PacketManagedSound.setTimeCode(this.id, timeCode), this.player);
      }
   }

   public void stop() {
      ManagedSoundRegistry.stop(this.player, this.id);
      Dispatcher.sendTo(PacketManagedSound.stop(this.id), this.player);
   }

   private ManagedSoundRegistry.State state() {
      return ManagedSoundRegistry.get(this.player, this.id);
   }
}
