package mchorse.mappet.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Server side mirror of the movement and key locks applied on the client by
 * {@link mchorse.mappet.client.ClientMovementLockState}.
 *
 * <p>The locks themselves are enforced on the client (the key binds are
 * released), so the server has to remember what it asked for in order to answer
 * {@code isJumpDisabled()}, {@code isSprintDisabled()},
 * {@code isWalkingDisabled()} and {@code isKeyDisabled()} from server side
 * scripts.</p>
 */
public class MappetMovementLock {
   private static final Map<UUID, Boolean> JUMPS = new HashMap<>();
   private static final Map<UUID, Boolean> SPRINTS = new HashMap<>();
   private static final Map<UUID, Boolean> WALKS = new HashMap<>();
   private static final Map<UUID, Set<String>> BINDS = new HashMap<>();

   public static void setJumpDisabled(UUID player, boolean disabled) {
      set(JUMPS, player, disabled);
   }

   public static boolean isJumpDisabled(UUID player) {
      return is(JUMPS, player);
   }

   public static void setSprintDisabled(UUID player, boolean disabled) {
      set(SPRINTS, player, disabled);
   }

   public static boolean isSprintDisabled(UUID player) {
      return is(SPRINTS, player);
   }

   public static void setWalkDisabled(UUID player, boolean disabled) {
      set(WALKS, player, disabled);
   }

   public static boolean isWalkDisabled(UUID player) {
      return is(WALKS, player);
   }

   public static void setBindLocked(UUID player, String bind, boolean locked) {
      if (player == null || bind == null || bind.isEmpty()) {
         return;
      }

      if (locked) {
         BINDS.computeIfAbsent(player, (key) -> new HashSet<String>()).add(bind);
      } else {
         Set<String> binds = BINDS.get(player);

         if (binds != null) {
            binds.remove(bind);

            if (binds.isEmpty()) {
               BINDS.remove(player);
            }
         }
      }
   }

   public static boolean isBindLocked(UUID player, String bind) {
      Set<String> binds = player == null ? null : BINDS.get(player);

      return binds != null && bind != null && binds.contains(bind);
   }

   public static void clear(UUID player) {
      if (player == null) {
         return;
      }

      JUMPS.remove(player);
      SPRINTS.remove(player);
      WALKS.remove(player);
      BINDS.remove(player);
   }

   private static void set(Map<UUID, Boolean> locks, UUID player, boolean disabled) {
      if (player == null) {
         return;
      }

      if (disabled) {
         locks.put(player, Boolean.TRUE);
      } else {
         locks.remove(player);
      }
   }

   private static boolean is(Map<UUID, Boolean> locks, UUID player) {
      return player != null && Boolean.TRUE.equals(locks.get(player));
   }
}
