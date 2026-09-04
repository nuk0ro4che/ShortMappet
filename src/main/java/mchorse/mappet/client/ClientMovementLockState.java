package mchorse.mappet.client;

import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_315;


public final class ClientMovementLockState {
   private static boolean jumpDisabled;
   private static boolean sprintDisabled;

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

   public static boolean isJumpDisabled() {
      return jumpDisabled;
   }

   public static boolean isSprintDisabled() {
      return sprintDisabled;
   }

   
   public static boolean shouldBlockKey(int key, int scancode) {
      class_310 client = class_310.method_1551();
      if (client == null || client.field_1690 == null) {
         return false;
      }

      class_315 options = client.field_1690;
      return jumpDisabled && matches(options, "key.jump", key, scancode) || sprintDisabled && matches(options, "key.sprint", key, scancode);
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
