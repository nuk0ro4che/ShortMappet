package mchorse.mappet.client;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_315;


public final class ClientMovementLockState {
   private static boolean jumpDisabled;
   private static boolean sprintDisabled;
   private static boolean walkDisabled;
   private static final Set<String> LOCKED_BINDS = new HashSet<String>();

   private ClientMovementLockState() {
   }

   public static void setJumpDisabled(boolean disabled) {
      jumpDisabled = disabled;
      enforce();
   }

   public static void setSprintDisabled(boolean disabled) {
      sprintDisabled = disabled;
      enforce();
   }

   public static void setWalkDisabled(boolean disabled) {
      walkDisabled = disabled;
      enforce();
   }

   public static void setBindLocked(String bind, boolean locked) {
      if (locked && bind != null && !bind.isEmpty()) {
         LOCKED_BINDS.add(bind);
      } else if (!locked) {
         LOCKED_BINDS.remove(bind);
      }
      enforce();
   }

   public static boolean isJumpDisabled() {
      return jumpDisabled;
   }

   public static boolean isSprintDisabled() {
      return sprintDisabled;
   }

   public static boolean isWalkDisabled() {
      return walkDisabled;
   }

   public static boolean isBindLocked(String bind) {
      return LOCKED_BINDS.contains(bind);
   }

   
   public static boolean shouldBlockKey(int key, int scancode) {
      class_310 client = class_310.method_1551();
      if (client == null || client.field_1690 == null) {
         return false;
      }

      class_315 options = client.field_1690;
      if (jumpDisabled && matches(options, "key.jump", key, scancode)) {
         return true;
      }
      if (sprintDisabled && matches(options, "key.sprint", key, scancode)) {
         return true;
      }
      if (walkDisabled && (matches(options, "key.forward", key, scancode) || matches(options, "key.back", key, scancode) || matches(options, "key.left", key, scancode) || matches(options, "key.right", key, scancode))) {
         return true;
      }
      for(String bind : LOCKED_BINDS) {
         if (matches(options, bind, key, scancode)) {
            return true;
         }
      }
      return false;
   }

   public static boolean shouldBlockMouse(int button) {
      class_310 client = class_310.method_1551();
      if (client == null || client.field_1690 == null || LOCKED_BINDS.isEmpty()) {
         return false;
      }

      if (client.field_1755 != null) {
         return false;
      }

      class_315 options = client.field_1690;
      for(String bind : LOCKED_BINDS) {
         for(class_304 binding : options.field_1839) {
            if (bind.equals(binding.method_1431()) && binding.method_1433(button)) {
               return true;
            }
         }
      }
      return false;
   }

   
   public static void enforce() {
      class_310 client = class_310.method_1551();
      if (client == null || client.field_1690 == null) {
         return;
      }

      class_315 options = client.field_1690;
      if (jumpDisabled) {
         release(options, "key.jump");
      }

      if (sprintDisabled) {
         release(options, "key.sprint");
         if (client.field_1724 != null) {
            client.field_1724.method_5728(false);
         }
      }

      if (walkDisabled) {
         release(options, "key.forward");
         release(options, "key.back");
         release(options, "key.left");
         release(options, "key.right");
      }

      for(String bind : LOCKED_BINDS) {
         release(options, bind);
      }
   }

   private static boolean matches(class_315 options, String id, int key, int scancode) {
      for(class_304 binding : options.field_1839) {
         if (id.equals(binding.method_1431()) && binding.method_1417(key, scancode)) {
            return true;
         }
      }

      return false;
   }

   private static void release(class_315 options, String id) {
      for(class_304 binding : options.field_1839) {
         if (id.equals(binding.method_1431())) {
            binding.method_23481(false);
         }
      }
   }
}
