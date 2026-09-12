package mchorse.mappet.api.scripts.code.sounds;

import mchorse.mappet.api.scripts.user.data.ScriptVector;
import mchorse.mappet.api.scripts.user.sounds.IScriptManagedSound;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import net.minecraft.class_1937;
import net.minecraft.class_3222;


public class ScriptWorldManagedSound implements IScriptManagedSound {
   private final class_1937 world;
   private final String id;

   public ScriptWorldManagedSound(class_1937 world, String id) {
      this.world = world;
      this.id = id;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            return state.name;
         }
      }

      return "";
   }

   public ScriptVector getPosition() {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            return new ScriptVector(state.x, state.y, state.z);
         }
      }

      return new ScriptVector(0.0D, 0.0D, 0.0D);
   }

   public float getVolume() {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            return state.volume;
         }
      }

      return 0.0F;
   }

   public void setPosition(double x, double y, double z) {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            ManagedSoundRegistry.update(player, this.id, x, y, z, state.volume);
            Dispatcher.sendTo(PacketManagedSound.update(this.id, x, y, z, state.volume), player);
         }
      }
   }

   public void setVolume(float volume) {
      float liveVolume = Math.max(0.0F, volume);
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            ManagedSoundRegistry.update(player, this.id, state.x, state.y, state.z, liveVolume);
            Dispatcher.sendTo(PacketManagedSound.update(this.id, state.x, state.y, state.z, liveVolume), player);
         }
      }
   }

   public double getTimeCode() {
      for(class_3222 player : this.players()) {
         if (ManagedSoundRegistry.has(player, this.id)) {
            Dispatcher.sendTo(PacketManagedSound.requestTimeCode(this.id), player);
            return ManagedSoundRegistry.getTimeCode(player, this.id);
         }
      }

      return 0.0D;
   }

   public void setTimeCode(double seconds) {
      double timeCode = Math.max(0.0D, seconds);
      for(class_3222 player : this.players()) {
         if (ManagedSoundRegistry.has(player, this.id)) {
            ManagedSoundRegistry.setTimeCode(player, this.id, timeCode);
            Dispatcher.sendTo(PacketManagedSound.setTimeCode(this.id, timeCode), player);
         }
      }
   }

   public boolean isPlaying() {
      for(class_3222 player : this.players()) {
         if (ManagedSoundRegistry.has(player, this.id)) {
            return true;
         }
      }

      return false;
   }

   public boolean isLooping() {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            return state.loop;
         }
      }

      return false;
   }

   public void loop(boolean loop) {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            ManagedSoundRegistry.loop(player, this.id, loop);
            Dispatcher.sendTo(PacketManagedSound.loop(this.id, loop), player);
         }
      }
   }

   public boolean isPaused() {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null && state.paused) {
            return true;
         }
      }

      return false;
   }

   public void pause() {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            ManagedSoundRegistry.pause(player, this.id, true);
            Dispatcher.sendTo(PacketManagedSound.pause(this.id), player);
         }
      }
   }

   public void resume() {
      for(class_3222 player : this.players()) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, this.id);
         if (state != null) {
            ManagedSoundRegistry.pause(player, this.id, false);
            Dispatcher.sendTo(PacketManagedSound.resume(this.id), player);
         }
      }
   }

   public void stop() {
      for(class_3222 player : this.players()) {
         if (ManagedSoundRegistry.has(player, this.id)) {
            ManagedSoundRegistry.stop(player, this.id);
            Dispatcher.sendTo(PacketManagedSound.stop(this.id), player);
         }
      }
   }

   private Iterable<class_3222> players() {
      return this.world.method_8503().method_3760().method_14571();
   }
}
