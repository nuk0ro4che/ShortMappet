package mchorse.mappet.compat.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Quaternionf;

public final class LegacyGlStateManager {
   private LegacyGlStateManager() {
   }

   private static class_4587 matrices() {
      return RenderSystem.getModelViewStack();
   }

   private static void apply() {
      RenderSystem.applyModelViewMatrix();
   }

   public static void pushMatrix() {
      matrices().method_22903();
      apply();
   }

   public static void popMatrix() {
      matrices().method_22909();
      apply();
   }

   public static void translate(double x, double y, double z) {
      matrices().method_22904(x, y, z);
      apply();
   }

   public static void scale(double x, double y, double z) {
      matrices().method_22905((float)x, (float)y, (float)z);
      apply();
   }

   public static void rotate(float angle, float x, float y, float z) {
      matrices().method_22907((new Quaternionf()).rotationAxis((float)Math.toRadians((double)angle), x, y, z));
      apply();
   }

   public static void color(float r, float g, float b) {
      color(r, g, b, 1.0F);
   }

   public static void color(float r, float g, float b, float a) {
      RenderSystem.setShaderColor(r, g, b, a);
   }

   public static void enableBlend() {
      RenderSystem.enableBlend();
   }

   public static void disableBlend() {
      RenderSystem.disableBlend();
   }

   public static void blendFunc(SourceFactor source, DestFactor destination) {
      RenderSystem.defaultBlendFunc();
   }

   public static void enableDepth() {
      RenderSystem.enableDepthTest();
   }

   public static void disableDepth() {
      RenderSystem.disableDepthTest();
   }

   public static void enableCull() {
      RenderSystem.enableCull();
   }

   public static void disableCull() {
      RenderSystem.disableCull();
   }

   public static void glLineWidth(float width) {
      RenderSystem.lineWidth(width);
   }

   public static void viewport(int x, int y, int width, int height) {
      RenderSystem.viewport(x, y, width, height);
   }

   public static void clear(int mask) {
      RenderSystem.clear(mask, LegacyGlStateManager.MinecraftOnOsx.VALUE);
   }

   public static void setActiveTexture(int texture) {
      RenderSystem.activeTexture(texture);
   }

   public static void enableLighting() {
   }

   public static void disableLighting() {
   }

   public static void enableTexture2D() {
   }

   public static void disableTexture2D() {
   }

   public static void enableAlpha() {
   }

   public static void disableAlpha() {
   }

   public static void enableRescaleNormal() {
   }

   public static void disableRescaleNormal() {
   }

   public static void shadeModel(int mode) {
   }

   public static void matrixMode(int mode) {
   }

   public static void loadIdentity() {
      matrices().method_34426();
      apply();
   }

   public static void ortho(double left, double right, double bottom, double top, double near, double far) {
   }

   public static enum SourceFactor {
      SRC_ALPHA;
      private static SourceFactor[] $values() {
         return new SourceFactor[]{SRC_ALPHA};
      }
   }

   public static enum DestFactor {
      ONE_MINUS_SRC_ALPHA;
      private static DestFactor[] $values() {
         return new DestFactor[]{ONE_MINUS_SRC_ALPHA};
      }
   }

   private static final class MinecraftOnOsx {
      private static final boolean VALUE;

      static {
         VALUE = class_310.field_1703;
      }
   }
}
