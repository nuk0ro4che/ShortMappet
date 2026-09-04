package mchorse.mappet.client;

public class CameraShakeHandler {
   private static boolean active;
   private static float pitch;
   private static float yaw;
   private static float roll;
   private static float frequency;
   private static long startedAt;
   private static long endsAt;

   public static void set(boolean active, int ticks, float pitch, float yaw, float roll, float frequency) {
      CameraShakeHandler.active = active;
      CameraShakeHandler.pitch = pitch;
      CameraShakeHandler.yaw = yaw;
      CameraShakeHandler.roll = roll;
      CameraShakeHandler.frequency = Math.max(0.0F, frequency);
      CameraShakeHandler.startedAt = System.currentTimeMillis();
      CameraShakeHandler.endsAt = ticks <= 0 ? 0L : CameraShakeHandler.startedAt + (long)ticks * 50L;
   }

   public static float getPitch() {
      return getOffset(pitch, 0.0D);
   }

   public static float getYaw() {
      return getOffset(yaw, 1.7D);
   }

   public static float getRoll() {
      return getOffset(roll, 3.4D);
   }

   private static float getOffset(float intensity, double phase) {
      if (!active) {
         return 0.0F;
      }

      long now = System.currentTimeMillis();
      if (endsAt > 0L && now >= endsAt) {
         active = false;
         return 0.0F;
      }

      double time = (double)(now - startedAt) / 1000.0D;
      return (float)((double)intensity * Math.sin(time * (double)frequency * 6.283185307179586D + phase));
   }
}
