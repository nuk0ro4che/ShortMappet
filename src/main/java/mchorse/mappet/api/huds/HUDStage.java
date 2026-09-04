package mchorse.mappet.api.huds;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mchorse.mappet.client.ClientFirstPersonLight;
import mchorse.mappet.compat.client.LegacyGlStateManager;
import mchorse.mappet.compat.client.LegacyOpenGlHelper;
import mchorse.mappet.mixins.LightmapTextureManagerAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1041;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_761;
import net.minecraft.class_8251;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class HUDStage {
   public Map<String, HUDScene> scenes = new LinkedHashMap();
   private List<RenderEntry> renderOrtho = new ArrayList();
   private List<RenderEntry> renderPerpsective = new ArrayList();
   private boolean ignoreF1;

   public HUDStage(boolean ignoreF1) {
      this.ignoreF1 = ignoreF1;
   }

   public void reset() {
      this.scenes.clear();
   }

   public void update(boolean allowExpiring) {
      this.scenes.values().removeIf((scene) -> scene.update(allowExpiring));
   }

   public void render(class_1041 resolution, float partialTicks) {
      class_310 mc = class_310.method_1551();
      this.renderOrtho.clear();
      this.enableGLStates();
      int w = resolution.method_4486();
      int h = resolution.method_4502();
      float lastX = LegacyOpenGlHelper.lastBrightnessX;
      float lastY = LegacyOpenGlHelper.lastBrightnessY;
      LegacyGlStateManager.clear(256);
      





      int framebufferWidth = resolution.method_4489();
      int framebufferHeight = resolution.method_4506();
      int vx = 0;
      int vy = 0;
      int vw = framebufferWidth;
      int vh = framebufferHeight;
      float aspect = (float)framebufferWidth / (float)framebufferHeight;
      float lastFov = Float.MIN_VALUE;
      int worldLight = this.getWorldLight(mc);
      LegacyGlStateManager.viewport(vx, vy, vw, vh);
      LegacyGlStateManager.pushMatrix();
      LegacyGlStateManager.loadIdentity();
      LegacyGlStateManager.translate((double)0.0F, (double)-1.0F, (double)-2.0F);

      for(HUDScene scene : this.scenes.values()) {
         if (!mc.field_1690.field_1842 || !scene.hide || this.ignoreF1) {
            if (lastFov != scene.fov) {
               RenderSystem.setProjectionMatrix((new Matrix4f()).setPerspective((float)Math.toRadians((double)scene.fov), aspect, 0.05F, 1000.0F), class_8251.field_43360);
               lastFov = scene.fov;
            }

            this.renderPerpsective.clear();

            for(HUDMorph morph : scene.morphs) {
               RenderEntry entry = new RenderEntry(morph, scene.worldLighting, scene.worldLightingIntensity);
               if (morph.ortho) {
                  this.renderOrtho.add(entry);
               } else {
                  this.renderPerpsective.add(entry);
               }
            }

            this.renderPerpsective.sort(this::depthSort);

            for(RenderEntry entry : this.renderPerpsective) {
               LightmapColor lightColor = entry.worldLighting ? this.getLightmapColor(mc, worldLight, entry.worldLightingIntensity) : LightmapColor.WHITE;
               entry.morph.render(resolution, partialTicks, 15728880, lightColor.red, lightColor.green, lightColor.blue);
            }
         }
      }

      LegacyGlStateManager.popMatrix();
      


      this.setupOrtho(mc, w, h, true);
      LegacyGlStateManager.clear(256);
      this.renderOrtho.sort(this::depthSort);

      for(RenderEntry entry : this.renderOrtho) {
         LightmapColor lightColor = entry.worldLighting ? this.getLightmapColor(mc, worldLight, entry.worldLightingIntensity) : LightmapColor.WHITE;
         entry.morph.render(resolution, partialTicks, 15728880, lightColor.red, lightColor.green, lightColor.blue);
      }

      this.disableGLStates();
      this.setupOrtho(mc, w, h, false);
      LegacyOpenGlHelper.setLightmapTextureCoords(33985, lastX, lastY);
   }

   private int getWorldLight(class_310 mc) {
      


      int handLight = ClientFirstPersonLight.get();
      if (handLight != 15728880 || mc.field_1687 == null || mc.field_1724 == null) {
         return handLight;
      }
      return class_761.method_23794(mc.field_1687, mc.field_1724.method_43260());
   }

   private LightmapColor getLightmapColor(class_310 mc, int packedLight, float intensity) {
      try {
         class_1011 image = ((LightmapTextureManagerAccessor)(Object)mc.field_1773.method_22974()).mappet$getLightmapImage();
         int block = Math.max(0, Math.min(15, packedLight >>> 4 & 15));
         int sky = Math.max(0, Math.min(15, packedLight >>> 20 & 15));
         int abgr = image.method_4315(block, sky);
         float red = (float)(abgr & 255) / 255.0F;
         float green = (float)(abgr >>> 8 & 255) / 255.0F;
         float blue = (float)(abgr >>> 16 & 255) / 255.0F;
         return new LightmapColor(this.scaleLightmapColor(red, intensity), this.scaleLightmapColor(green, intensity), this.scaleLightmapColor(blue, intensity));
      } catch (Throwable ignored) {
         

         return LightmapColor.WHITE;
      }
   }

   private float scaleLightmapColor(float color, float intensity) {
      float factor = Math.max(0.0F, Math.min(4.0F, intensity));
      


      float compressed = 0.28F + Math.max(0.0F, Math.min(1.0F, color)) * 0.72F;
      return factor <= 1.0F ? compressed * factor : compressed + (1.0F - compressed) * (factor - 1.0F) / 3.0F;
   }

   private int scalePackedLight(int packedLight, float intensity) {
      int block = this.boostLightChannel(packedLight >>> 4 & 15, intensity);
      int sky = this.boostLightChannel(packedLight >>> 20 & 15, intensity);
      

      return block << 4 | sky << 20;
   }

   private int boostLightChannel(int channel, float intensity) {
      float factor = Math.max(0.0F, Math.min(4.0F, intensity));
      

      float worldLight = (float)Math.max(0, Math.min(15, channel));
      float value;
      if (factor <= 1.0F) {
         value = worldLight * factor;
      } else {
         

         value = worldLight + (15.0F - worldLight) * (factor - 1.0F) / 3.0F;
      }
      return Math.min(15, Math.max(0, Math.round(value)));
   }

   private void applyLighting(boolean worldLighting, int packedWorldLight, float intensity) {
      if (worldLighting) {
         float blockLight = (float)this.boostLightChannel(packedWorldLight >>> 4 & 15, intensity) * 16.0F;
         float skyLight = (float)this.boostLightChannel(packedWorldLight >>> 20 & 15, intensity) * 16.0F;
         LegacyOpenGlHelper.setLightmapTextureCoords(33985, blockLight, skyLight);
      } else {
         LegacyOpenGlHelper.setLightmapTextureCoords(33985, 240.0F, 240.0F);
      }
   }

   private int depthSort(RenderEntry a, RenderEntry b) {
      float diff = a.morph.translate.z - b.morph.translate.z;
      if (diff == 0.0F) {
         return 0;
      } else {
         return diff < 0.0F ? -1 : 1;
      }
   }

   private static class LightmapColor {
      public static final LightmapColor WHITE = new LightmapColor(1.0F, 1.0F, 1.0F);
      public final float red;
      public final float green;
      public final float blue;

      public LightmapColor(float red, float green, float blue) {
         this.red = red;
         this.green = green;
         this.blue = blue;
      }
   }

   private static class RenderEntry {
      public final HUDMorph morph;
      public final boolean worldLighting;
      public final float worldLightingIntensity;

      public RenderEntry(HUDMorph morph, boolean worldLighting, float worldLightingIntensity) {
         this.morph = morph;
         this.worldLighting = worldLighting;
         this.worldLightingIntensity = worldLightingIntensity;
      }
   }

   private void setupOrtho(class_310 mc, int w, int h, boolean flip) {
      class_1041 window = mc.method_22683();
      LegacyGlStateManager.viewport(0, 0, window.method_4489(), window.method_4506());
      if (flip) {
         RenderSystem.setProjectionMatrix((new Matrix4f()).setOrtho(0.0F, (float)w, 0.0F, (float)h, 1000.0F, 3000000.0F), class_8251.field_43361);
      } else {
         RenderSystem.setProjectionMatrix((new Matrix4f()).setOrtho(0.0F, (float)w, (float)h, 0.0F, 1000.0F, 3000000.0F), class_8251.field_43361);
      }

   }

   private void enableGLStates() {
      class_308.method_24211();
      LegacyGlStateManager.enableAlpha();
      LegacyGlStateManager.enableRescaleNormal();
      LegacyGlStateManager.enableDepth();
      LegacyGlStateManager.disableCull();
      LegacyGlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void disableGLStates() {
      LegacyGlStateManager.enableCull();
      LegacyGlStateManager.disableDepth();
      LegacyGlStateManager.disableRescaleNormal();
      LegacyGlStateManager.disableAlpha();
      class_308.method_24210();
      LegacyGlStateManager.setActiveTexture(33985);
      LegacyGlStateManager.disableTexture2D();
      LegacyGlStateManager.setActiveTexture(33984);
   }
}
