package mchorse.mappet.api.scripts.code.client;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientMouseSensitivityCache {
   private static final ConcurrentHashMap<UUID, Double> VALUES = new ConcurrentHashMap<>();

   private ClientMouseSensitivityCache() {
   }

   public static double get(UUID player) {
      return VALUES.getOrDefault(player, 0.5D);
   }

   public static void set(UUID player, double value) {
      VALUES.put(player, value);
   }

   public static void remove(UUID player) {
      VALUES.remove(player);
   }
}
