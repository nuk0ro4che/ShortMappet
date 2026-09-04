package mchorse.mappet.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import mchorse.mappet.network.common.scripts.PacketEntityTransition;
import mchorse.mclib.utils.Interpolation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_310;



@Environment(EnvType.CLIENT)
public final class ClientEntityTransitions {
   private static final Map<Integer, Transition> transitions = new HashMap();

   private ClientEntityTransitions() {
   }

   public static void start(class_1937 world, PacketEntityTransition packet) {
      if (world == null || packet == null) {
         return;
      }

      class_1297 entity = world.method_8469(packet.entityId);
      if (entity == null) {
         return;
      }

      long now = System.nanoTime();
      Transition transition = transitions.get(packet.entityId);
      if (transition == null) {
         transition = new Transition(packet.entityId);
         transitions.put(packet.entityId, transition);
      }

      transition.update(entity, now);
      transition.set(entity, packet, now);
   }

   public static void render(float tickDelta) {
      long now = System.nanoTime();
      class_310 client = class_310.method_1551();
      class_1937 world = client.field_1687;
      if (world == null || transitions.isEmpty()) {
         return;
      }

      Iterator<Transition> iterator = transitions.values().iterator();
      while (iterator.hasNext()) {
         Transition transition = iterator.next();
         class_1297 entity = world.method_8469(transition.entityId);
         if (entity == null) {
            iterator.remove();
         } else if (!transition.update(entity, now)) {
            iterator.remove();
         }
      }
   }

   public static void clear() {
      transitions.clear();
   }

   private static class Transition {
      private final int entityId;
      private boolean position;
      private boolean rotation;
      private boolean relativeRotation;
      private float appliedPitch;
      private float appliedYaw;
      private float appliedYawHead;
      private double fromX;
      private double fromY;
      private double fromZ;
      private double toX;
      private double toY;
      private double toZ;
      private float fromPitch;
      private float fromYaw;
      private float fromYawHead;
      private float toPitch;
      private float toYaw;
      private float toYawHead;
      private long started;
      private long finished;
      private Interpolation interpolation = Interpolation.LINEAR;

      private Transition(int entityId) {
         this.entityId = entityId;
      }

      private void set(class_1297 entity, PacketEntityTransition packet, long now) {
         this.position = packet.position;
         this.rotation = packet.rotation;
         this.relativeRotation = packet.relativeRotation;
         this.appliedPitch = 0.0F;
         this.appliedYaw = 0.0F;
         this.appliedYawHead = 0.0F;
         this.fromX = entity.method_23317();
         this.fromY = entity.method_23318();
         this.fromZ = entity.method_23321();
         this.toX = packet.x;
         this.toY = packet.y;
         this.toZ = packet.z;
         this.fromPitch = entity.method_36455();
         this.fromYaw = entity.method_36454();
         this.fromYawHead = entity.method_5791();
         

         this.toPitch = packet.pitch;
         this.toYaw = packet.relativeRotation ? packet.yaw : closestRotation(this.fromYaw, packet.yaw);
         this.toYawHead = packet.relativeRotation ? packet.yawHead : closestRotation(this.fromYawHead, packet.yawHead);
         this.interpolation = resolveInterpolation(packet.interpolation);
         this.started = now;
         this.finished = now + (long)Math.max(0, packet.durationTicks) * 50000000L;

         if (this.finished <= this.started) {
            this.apply(entity, 1.0D);
         }
      }

      
      private boolean update(class_1297 entity, long now) {
         if (this.finished <= this.started) {
            return false;
         }

         double progress = Math.max(0.0D, Math.min(1.0D, (double)(now - this.started) / (double)(this.finished - this.started)));
         this.apply(entity, this.interpolation.interpolate(0.0D, 1.0D, progress));
         return progress < 1.0D;
      }

      private void apply(class_1297 entity, double progress) {
         if (this.position) {
            double x = lerp(this.fromX, this.toX, progress);
            double y = lerp(this.fromY, this.toY, progress);
            double z = lerp(this.fromZ, this.toZ, progress);
            


            entity.field_6014 = x;
            entity.field_6036 = y;
            entity.field_5969 = z;
            entity.method_5814(x, y, z);
         }

         if (this.rotation) {
            float yaw;
            float pitch;
            float yawHead;
            if (this.relativeRotation) {
               float deltaYaw = (float)lerp(0.0F, this.toYaw, progress);
               float deltaPitch = (float)lerp(0.0F, this.toPitch, progress);
               float deltaHead = (float)lerp(0.0F, this.toYawHead, progress);
               yaw = entity.method_36454() - this.appliedYaw + deltaYaw;
               pitch = entity.method_36455() - this.appliedPitch + deltaPitch;
               yawHead = entity.method_5791() - this.appliedYawHead + deltaHead;
               this.appliedYaw = deltaYaw;
               this.appliedPitch = deltaPitch;
               this.appliedYawHead = deltaHead;
            } else {
               yaw = (float)lerp(this.fromYaw, this.toYaw, progress);
               pitch = (float)lerp(this.fromPitch, this.toPitch, progress);
               yawHead = (float)lerp(this.fromYawHead, this.toYawHead, progress);
            }
            entity.field_5982 = entity.method_36454();
            entity.field_6004 = entity.method_36455();
            entity.method_5808(entity.method_23317(), entity.method_23318(), entity.method_23321(), yaw, pitch);
            entity.method_5847(yawHead);
            if (entity instanceof class_1309) {
               class_1309 living = (class_1309)entity;
               living.field_6259 = living.method_5791();
               living.field_6283 = yawHead;
            }
         }
      }

      private static double lerp(double from, double to, double progress) {
         return from + (to - from) * progress;
      }

      private static float closestRotation(float from, float target) {
         float delta = (target - from) % 360.0F;
         if (delta >= 180.0F) {
            delta -= 360.0F;
         } else if (delta < -180.0F) {
            delta += 360.0F;
         }
         return from + delta;
      }

      private static Interpolation resolveInterpolation(String name) {
         try {
            return Interpolation.valueOf(name == null ? "LINEAR" : name.toUpperCase(Locale.ROOT));
         } catch (IllegalArgumentException exception) {
            return Interpolation.LINEAR;
         }
      }
   }
}
