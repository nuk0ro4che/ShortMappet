package mchorse.mappet.client;

/**
 * Client side flag that allows sprinting regardless of the hunger level.
 */
public final class ClientSprintHungerState {
   private static boolean disabled;

   private ClientSprintHungerState() {
   }

   public static void setDisabled(boolean value) {
      disabled = value;
   }

   public static boolean isDisabled() {
      return disabled;
   }

   public static void reset() {
      disabled = false;
   }
}
