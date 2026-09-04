package mchorse.mappet.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class FirstPersonBodyState {
   private static boolean enabled;

   private FirstPersonBodyState() {
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static void setEnabled(boolean value) {
      enabled = value;
   }

   public static void reset() {
      enabled = false;
   }
}
