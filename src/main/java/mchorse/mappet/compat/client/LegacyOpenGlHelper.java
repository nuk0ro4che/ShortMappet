package mchorse.mappet.compat.client;

import org.lwjgl.opengl.GL20;

public final class LegacyOpenGlHelper {
   public static final int defaultTexUnit = 33984;
   public static final int lightmapTexUnit = 33985;
   public static float lastBrightnessX;
   public static float lastBrightnessY;

   private LegacyOpenGlHelper() {
   }

   public static void setLightmapTextureCoords(int unit, float x, float y) {
      lastBrightnessX = x;
      lastBrightnessY = y;
   }

   public static void glUseProgram(int program) {
      GL20.glUseProgram(program);
   }
}
