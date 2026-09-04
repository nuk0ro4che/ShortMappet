package mchorse.mappet.hand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1657;

public final class Hands {
   private static final Map<UUID, HandState> STATES = new ConcurrentHashMap();

   private Hands() {
   }

   public static HandState get(class_1657 player) {
      return get(player.method_5667());
   }

   public static HandState get(UUID uuid) {
      return STATES.computeIfAbsent(uuid, (ignored) -> new HandState());
   }

   public static void set(UUID uuid, HandState state) {
      STATES.put(uuid, state);
   }

   public static void remove(UUID uuid) {
      STATES.remove(uuid);
   }
}
