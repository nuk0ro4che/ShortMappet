package mchorse.mappet.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server side mirror of the hunger sprint bypass applied on the client by
 * {@link mchorse.mappet.client.ClientSprintHungerState}.
 *
 * <p>Vanilla only allows sprinting while the food level is above six. The
 * client enforces the bypass, while the server keeps the flag so server side
 * scripts can query {@code isSprintHungerDisabled()}.</p>
 */
public class MappetSprintHunger {
   private static final Map<UUID, Boolean> DISABLED = new HashMap<>();

   public static void setDisabled(UUID player, boolean disabled) {
      if (player == null) {
         return;
      }

      if (disabled) {
         DISABLED.put(player, Boolean.TRUE);
      } else {
         DISABLED.remove(player);
      }
   }

   public static boolean isDisabled(UUID player) {
      return player != null && Boolean.TRUE.equals(DISABLED.get(player));
   }

   public static void clear(UUID player) {
      if (player != null) {
         DISABLED.remove(player);
      }
   }
}
