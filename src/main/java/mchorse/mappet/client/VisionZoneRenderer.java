package mchorse.mappet.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mchorse.mappet.api.vision.VisionZone;
import mchorse.mappet.api.vision.VisionZoneManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.class_1297;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_5498;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class VisionZoneRenderer {
   public static void render(WorldRenderContext context) {
      class_310 mc = class_310.method_1551();

      if (!mc.field_1690.field_1866 || VisionZoneManager.clientZones.isEmpty() || context.consumers() == null || mc.field_1687 == null) {
         return;
      }

      List<class_1297> entities = new ArrayList<class_1297>();

      for (class_1297 entity : mc.field_1687.method_18456()) {
         entities.add(entity);
      }

      for (UUID uuid : VisionZoneManager.clientZones.keySet()) {
         VisionZone zone = VisionZoneManager.getClient(uuid);

         if (zone == null || (zone.shape == VisionZone.Shape.POLYGON && zone.points.size() < 3)) {
            continue;
         }

         class_1297 owner = null;

         for (class_1297 entity : entities) {
            if (entity.method_5667().equals(uuid)) {
               owner = entity;
               break;
            }
         }

         if (owner == null) {
            continue;
         }

         if (owner == mc.field_1724 && mc.field_1690.method_31044() == class_5498.field_26664) {
            continue;
         }

         double ex = owner.method_23317();
         double ey = owner.method_23318();
         double ez = owner.method_23321();
         double yaw = (double)owner.method_36454();
         double pitch = (double)owner.method_36455();

         boolean inside = false;

         if (mc.field_1724 != null && mc.field_1687.method_8597() == owner.method_37908().method_8597()) {
            inside = zone.contains(ex, ez, ey, yaw, pitch, mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
         }

         double[] red = inside ? new double[]{1.0, 0.25, 0.25} : new double[]{0.25, 1.0, 0.3};
         double[] outline = VisionZoneRenderer.getOutline(zone);

         if (outline == null) {
            continue;
         }

         class_243 cam = context.camera().method_19326();
         float camX = (float)cam.field_1352;
         float camY = (float)cam.field_1351;
         float camZ = (float)cam.field_1350;

         double ay = Math.toRadians(yaw + zone.yawOffset);
         double sinY = Math.sin(ay);
         double cosY = Math.cos(ay);
         double ap = Math.toRadians(pitch);
         double sinP = Math.sin(ap);
         double cosP = Math.cos(ap);

         double pivot = ey + zone.eyeOffset;
         double halfY = (zone.maxHeight - zone.minHeight) / 2.0;
         boolean frustum = zone.viewAngleV > 0.0;
         boolean isCone = frustum && zone.viewAngle > 0.0 && zone.viewAngle < 360.0;
         double tanV = frustum ? Math.tan(Math.toRadians(zone.viewAngleV / 2.0)) : 0.0;
         boolean hasHeight = frustum || Math.abs(zone.maxHeight - zone.minHeight) > 0.001;

         class_4587 matrices = context.matrixStack();
         class_4587.class_4665 entry = matrices.method_23760();
         org.joml.Matrix4f matrix = entry.method_23761();
         class_4588 buffer = context.consumers().getBuffer(class_1921.method_23594());

         if (isCone) {
            VisionZoneRenderer.cone(buffer, matrix, camX, camY, camZ, ex, pivot, ez, sinY, cosY, sinP, cosP, zone, red);
            continue;
         }

         for (int slice = 0; slice < 2; slice++) {
            double sign = slice == 0 ? -1.0 : 1.0;

            for (int i = 0; i < outline.length; i += 2) {
               int next = (i + 2) % outline.length;
               double lx1 = outline[i];
               double lz1 = outline[i + 1];
               double lx2 = outline[next];
               double lz2 = outline[next + 1];
               double ly1 = frustum ? sign * tanV * Math.sqrt(lx1 * lx1 + lz1 * lz1) : sign * halfY;
               double ly2 = frustum ? sign * tanV * Math.sqrt(lx2 * lx2 + lz2 * lz2) : sign * halfY;

               VisionZoneRenderer.line(buffer, matrix, camX, camY, camZ, ex, pivot, ez, sinY, cosY, sinP, cosP, lx1, ly1, lz1, lx2, ly2, lz2, red);
            }
         }

         if (hasHeight) {
            for (int i = 0; i < outline.length; i += 2) {
               double lx = outline[i];
               double lz = outline[i + 1];
               double ly = frustum ? tanV * Math.sqrt(lx * lx + lz * lz) : halfY;

               VisionZoneRenderer.line(buffer, matrix, camX, camY, camZ, ex, pivot, ez, sinY, cosY, sinP, cosP, lx, -ly, lz, lx, ly, lz, red);
            }
         }
      }
   }

   private static void cone(class_4588 buffer, Matrix4f matrix, float camX, float camY, float camZ, double ex, double ey, double ez, double sinY, double cosY, double sinP, double cosP, VisionZone zone, double[] color) {
      double r = zone.radius;

      if (r <= 0.001) {
         return;
      }

      double halfH = Math.toRadians(zone.viewAngle / 2.0);
      double halfV = Math.toRadians(zone.viewAngleV / 2.0);
      int steps = 32;
      int spokes = 8;
      double[] dists = new double[]{r * 0.25, r * 0.6, r};

      for (double d : dists) {
         double uH = d * Math.tan(halfH);
         double uV = d * Math.tan(halfV);

         for (int i = 0; i < steps; i++) {
            double t = (double)i / (double)steps * Math.PI * 2.0;
            double t2 = (double)(i + 1) / (double)steps * Math.PI * 2.0;

            VisionZoneRenderer.line(buffer, matrix, camX, camY, camZ, ex, ey, ez, sinY, cosY, sinP, cosP, uH * Math.cos(t), uV * Math.sin(t), d, uH * Math.cos(t2), uV * Math.sin(t2), d, color);
         }
      }

      for (int k = 0; k < spokes; k++) {
         double t = (double)k / (double)spokes * Math.PI * 2.0;

         VisionZoneRenderer.line(buffer, matrix, camX, camY, camZ, ex, ey, ez, sinY, cosY, sinP, cosP, 0, 0, 0, r * Math.tan(halfH) * Math.cos(t), r * Math.tan(halfV) * Math.sin(t), r, color);
      }
   }

   private static double[] getOutline(VisionZone zone) {
      if (zone.shape == VisionZone.Shape.POLYGON) {
         double[] outline = new double[zone.points.size() * 2];

         for (int i = 0; i < zone.points.size(); i++) {
            outline[i * 2] = zone.points.get(i).x;
            outline[i * 2 + 1] = zone.points.get(i).z;
         }

         return outline;
      }

      int steps = 32;

      if (zone.viewAngle >= 360.0 || zone.viewAngle <= 0.0) {
         double[] outline = new double[steps * 2];

         for (int i = 0; i < steps; i++) {
            double t = (double)i / (double)steps * Math.PI * 2.0;
            outline[i * 2] = Math.sin(t) * zone.radius;
            outline[i * 2 + 1] = Math.cos(t) * zone.radius;
         }

         return outline;
      }

      double half = Math.toRadians(zone.viewAngle / 2.0);
      double arc = Math.toRadians(zone.viewAngle);
      double[] outline = new double[(steps + 3) * 2];

      outline[0] = 0;
      outline[1] = 0;

      for (int i = 0; i <= steps; i++) {
         double t = -half + arc * (double)i / (double)steps;
         outline[(i + 1) * 2] = Math.sin(t) * zone.radius;
         outline[(i + 1) * 2 + 1] = Math.cos(t) * zone.radius;
      }

      return outline;
   }

   private static void line(class_4588 buffer, Matrix4f matrix, float camX, float camY, float camZ, double ex, double ey, double ez, double sinY, double cosY, double sinP, double cosP, double lx1, double ly1, double lz1, double lx2, double ly2, double lz2, double[] color) {
      int r = (int)(color[0] * 255.0);
      int g = (int)(color[1] * 255.0);
      int b = (int)(color[2] * 255.0);

      VisionZoneRenderer.vertex(buffer, matrix, camX, camY, camZ, ex, ey, ez, sinY, cosY, sinP, cosP, lx1, ly1, lz1, r, g, b);
      VisionZoneRenderer.vertex(buffer, matrix, camX, camY, camZ, ex, ey, ez, sinY, cosY, sinP, cosP, lx2, ly2, lz2, r, g, b);
   }

   private static void vertex(class_4588 buffer, Matrix4f matrix, float camX, float camY, float camZ, double ex, double ey, double ez, double sinY, double cosY, double sinP, double cosP, double lx, double ly, double lz, int r, int g, int b) {
      double x1 = lx;
      double y1 = ly * cosP - lz * sinP;
      double z1 = ly * sinP + lz * cosP;
      double wx = x1 * cosY - z1 * sinY;
      double wz = x1 * sinY + z1 * cosY;

      buffer.method_22918(matrix, (float)(ex + wx - camX), (float)(ey + y1 - camY), (float)(ez + wz - camZ)).method_1336(r, g, b, 255).method_22914(0, 1, 0).method_1344();
   }
}