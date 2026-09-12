package mchorse.mappet.api.scripts.code.sounds;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_3222;


public final class ManagedSoundRegistry {
   public static final class State {
      public final String name;
      public final boolean staticSound;
      public final float pitch;
      public final int entityId;
      public boolean paused;
      public boolean loop;
      public double x;
      public double y;
      public double z;
      public float volume;

      private State(String name, boolean staticSound, double x, double y, double z, float volume, float pitch, int entityId) {
         this.name = name;
         this.staticSound = staticSound;
         this.x = x;
         this.y = y;
         this.z = z;
         this.volume = volume;
         this.pitch = pitch;
         this.entityId = entityId;
      }
   }

   private static final Map<UUID, Map<String, State>> ACTIVE = new HashMap();
   private static final Map<UUID, Map<String, Double>> TIME_CODES = new HashMap();

   private ManagedSoundRegistry() {
   }

   public static void play(class_3222 player, String id, String name, boolean staticSound, double x, double y, double z, float volume, float pitch) {
      play(player, id, name, staticSound, x, y, z, volume, pitch, -1);
   }

   public static void play(class_3222 player, String id, String name, boolean staticSound, double x, double y, double z, float volume, float pitch, int entityId) {
      ACTIVE.computeIfAbsent(player.method_5667(), (uuid) -> new HashMap()).put(id, new State(name, staticSound, x, y, z, Math.max(0.0F, volume), pitch, entityId));
      TIME_CODES.computeIfAbsent(player.method_5667(), (uuid) -> new HashMap()).put(id, 0.0D);
   }

   public static State get(class_3222 player, String id) {
      Map<String, State> sounds = ACTIVE.get(player.method_5667());
      return sounds == null ? null : sounds.get(id);
   }

   public static boolean has(class_3222 player, String id) {
      return get(player, id) != null;
   }

   public static State update(class_3222 player, String id, double x, double y, double z, float volume) {
      State state = get(player, id);
      if (state != null) {
         state.x = x;
         state.y = y;
         state.z = z;
         state.volume = Math.max(0.0F, volume);
      }

      return state;
   }

public static void pause(class_3222 player, String id, boolean paused) {
      State state = get(player, id);
      if (state != null) {
         state.paused = paused;
      }
    }

   public static void loop(class_3222 player, String id, boolean loop) {
      State state = get(player, id);
      if (state != null) {
         state.loop = loop;
      }
    }

   public static void stop(class_3222 player, String id) {
      remove(ACTIVE, player.method_5667(), id);
      remove(TIME_CODES, player.method_5667(), id);
   }

   
   public static boolean finish(class_3222 player, String id, String name) {
      State state = get(player, id);
      if (state == null || !name.equals(state.name)) {
         return false;
      }

      stop(player, id);
      return true;
   }

   public static void setTimeCode(class_3222 player, String id, double timeCode) {
      if (has(player, id)) {
         TIME_CODES.computeIfAbsent(player.method_5667(), (uuid) -> new HashMap()).put(id, Math.max(0.0D, timeCode));
      }
   }

   public static double getTimeCode(class_3222 player, String id) {
      Map<String, Double> timeCodes = TIME_CODES.get(player.method_5667());
      return timeCodes == null ? 0.0D : timeCodes.getOrDefault(id, 0.0D);
   }

   public static void forget(class_3222 player) {
      ACTIVE.remove(player.method_5667());
      TIME_CODES.remove(player.method_5667());
   }

   private static <T> void remove(Map<UUID, Map<String, T>> map, UUID uuid, String id) {
      Map<String, T> values = map.get(uuid);
      if (values != null) {
         values.remove(id);
         if (values.isEmpty()) {
            map.remove(uuid);
         }
      }
   }
}
