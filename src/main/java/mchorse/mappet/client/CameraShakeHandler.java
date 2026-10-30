package mchorse.mappet.client;

public class CameraShakeHandler {
   private static boolean active;
   private static float pitch;
   private static float yaw;
   private static float roll;
   private static float frequency;
   private static long endsAt;
   private static long lastUpdate;
   private static double phase;
   private static long fadeStartedAt;
   private static long fadeEndsAt;

   public static void set(boolean active, boolean restart, int ticks, float pitch, float yaw, float roll, float frequency, int fadeTicks) {
      long now = System.currentTimeMillis();
      boolean wasActive = CameraShakeHandler.active && (CameraShakeHandler.endsAt == 0L || now < CameraShakeHandler.endsAt);

      if (fadeEndsAt > 0L && now >= fadeEndsAt) {
         fadeStartedAt = 0L;
         fadeEndsAt = 0L;
      }

      CameraShakeHandler.active = active;
      CameraShakeHandler.pitch = pitch;
      CameraShakeHandler.yaw = yaw;
      CameraShakeHandler.roll = roll;

      if (restart || !wasActive) {
         CameraShakeHandler.lastUpdate = now;
         CameraShakeHandler.phase = 0.0D;
      } else {
         advancePhase(now);
      }

      if (!active || restart) {
         fadeStartedAt = 0L;
         fadeEndsAt = 0L;
      }

      CameraShakeHandler.frequency = Math.max(0.0F, frequency);
      CameraShakeHandler.endsAt = ticks <= 0 ? 0L : now + (long)ticks * 50L;

      if (fadeTicks > 0) {
         fadeStartedAt = now;
         fadeEndsAt = now + (long)fadeTicks * 50L;
         CameraShakeHandler.endsAt = fadeEndsAt;
      }
   }

   private static void advancePhase(long now) {
      if (CameraShakeHandler.lastUpdate == 0L) {
         CameraShakeHandler.lastUpdate = now;
         return;
      }

      long delta = now - CameraShakeHandler.lastUpdate;
      if (delta > 0L) {
         CameraShakeHandler.phase += (double)delta / 1000.0D * (double)CameraShakeHandler.frequency * 6.283185307179586D;
         CameraShakeHandler.lastUpdate = now;
      }
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

   private static float getOffset(float intensity, double offset) {
      if (!active) {
         return 0.0F;
      }

      long now = System.currentTimeMillis();
      if (endsAt > 0L && now >= endsAt) {
         active = false;
         fadeEndsAt = 0L;
         return 0.0F;
      }

      advancePhase(now);

      double fade = 1.0D;

      if (fadeEndsAt > 0L) {
         long duration = fadeEndsAt - fadeStartedAt;
         long remaining = fadeEndsAt - now;

         if (duration <= 0L || remaining <= 0L) {
            active = false;
            fadeEndsAt = 0L;
            return 0.0F;
         }

         double t = (double)remaining / (double)duration;
         fade = t * t * (3.0D - 2.0D * t);
      }

      return (float)((double)intensity * fade * Math.sin(phase + offset));
   }
}
