package mchorse.mappet.hand;

import mchorse.metamorph.api.morphs.AbstractMorph;

public class HandState {
   public final Side main = new Side(true, true);
   public final Side off = new Side(false, true);

   public Side get(int side) {
      return side <= 0 ? this.main : this.off;
   }

   public static class Side {
      public boolean renderArm;
      public boolean renderItem;
      public double x;
      public double y;
      public double z;
      public double pitch;
      public double yaw;
      public double roll;
      public AbstractMorph morph;
      public final Transition position = new Transition();
      public final Transition rotation = new Transition();

      public Side(boolean renderArm, boolean renderItem) {
         this.renderArm = renderArm;
         this.renderItem = renderItem;
      }

      public void update(long now) {
         if (this.position.finished > this.position.started) {
            double amount = this.position.amount(now);
            this.x = lerp(this.position.fromX, this.position.toX, amount);
            this.y = lerp(this.position.fromY, this.position.toY, amount);
            this.z = lerp(this.position.fromZ, this.position.toZ, amount);
            if (now >= this.position.finished) this.position.clear();
         }

         if (this.rotation.finished > this.rotation.started) {
            double amount = this.rotation.amount(now);
            this.pitch = lerp(this.rotation.fromX, this.rotation.toX, amount);
            this.yaw = lerp(this.rotation.fromY, this.rotation.toY, amount);
            this.roll = lerp(this.rotation.fromZ, this.rotation.toZ, amount);
            if (now >= this.rotation.finished) this.rotation.clear();
         }
      }

      public void moveTo(String interpolation, int ticks, double x, double y, double z, long now) {
         this.update(now);
         this.position.set(this.x, this.y, this.z, x, y, z, interpolation, now, ticks);
      }

      public void rotateTo(String interpolation, int ticks, double pitch, double yaw, double roll, long now) {
         this.update(now);
         this.rotation.set(this.pitch, this.yaw, this.roll, pitch, yaw, roll, interpolation, now, ticks);
      }

      public void reset(boolean main) {
         this.renderArm = main;
         this.renderItem = true;
         this.x = this.y = this.z = 0.0D;
         this.pitch = this.yaw = this.roll = 0.0D;
         this.morph = null;
         this.position.clear();
         this.rotation.clear();
      }
   }

   public static class Transition {
      public double fromX;
      public double fromY;
      public double fromZ;
      public double toX;
      public double toY;
      public double toZ;
      public long started;
      public long finished;
      public String interpolation = "linear";

      public void set(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, String interpolation, long now, int ticks) {
         this.fromX = fromX; this.fromY = fromY; this.fromZ = fromZ;
         this.toX = toX; this.toY = toY; this.toZ = toZ;
         this.started = now;
         this.finished = now + Math.max(0, ticks) * 50L;
         this.interpolation = interpolation == null ? "linear" : interpolation;
      }

      public boolean active(long now) {
         return this.finished > now;
      }

      public double amount(long now) {
         if (this.finished <= this.started) return 1.0D;
         return apply(this.interpolation, Math.max(0.0D, Math.min(1.0D, (double)(now - this.started) / (double)(this.finished - this.started))));
      }

      public void clear() {
         this.started = this.finished = 0L;
      }
   }

   private static double lerp(double from, double to, double amount) {
      return from + (to - from) * amount;
   }

   private static double apply(String interpolation, double t) {
      String key = interpolation.toLowerCase();
      if (key.equals("quadin") || key.equals("easein")) return t * t;
      if (key.equals("quadout") || key.equals("easeout")) return 1.0D - (1.0D - t) * (1.0D - t);
      if (key.equals("quadinout") || key.equals("easeinout")) return t < 0.5D ? 2.0D * t * t : 1.0D - Math.pow(-2.0D * t + 2.0D, 2.0D) / 2.0D;
      return t;
   }
}
