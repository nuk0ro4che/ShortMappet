package mchorse.mappet.mixins;

import mchorse.mappet.client.HudCapture;
import mchorse.mappet.client.HudCustomState;
import net.minecraft.class_1058;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_332.class)
public abstract class DrawContextMixin {
   private static boolean mappet$reentrant;

   private static void mappet$cancel(CallbackInfo ci) {
      mappet$reentrant = true;

      try {
         ci.cancel();
      } finally {
         mappet$reentrant = false;
      }
   }

   private static int[] mappet$offsetScale(String id, int width, int height) {
      int dx = HudCustomState.getX(id) - HudCapture.appliedX(id);
      int dy = HudCustomState.getY(id) - HudCapture.appliedY(id);
      float scale = HudCustomState.getScale(id);
      int newWidth = width;
      int newHeight = height;
      if (scale != 1.0F && scale > 0.05F) {
         newWidth = (int)(width * scale);
         newHeight = (int)(height * scale);
      }

      if (dx == 0 && dy == 0 && newWidth == width && newHeight == height) {
         return null;
      }

      return new int[]{dx, dy, newWidth, newHeight};
   }

   @Inject(method = "method_25302", at = @At("HEAD"), cancellable = true)
   private void mappet$captureDrawTexture(class_2960 texture, int x, int y, int u, int v, int width, int height, CallbackInfo ci) {
      if (mappet$reentrant) {
         return;
      }

      class_332 self = (class_332)(Object)this;
      String id = HudCapture.drawAndKey(self, texture, x, y, width, height, false);
      if (id == null) {
         return;
      }

      if (HudCustomState.hasTransform(id) && !HudCustomState.isVisible(id)) {
         mappet$cancel(ci);
         return;
      }

      int[] transform = mappet$offsetScale(id, width, height);
      if (transform != null) {
         mappet$reentrant = true;

         try {
            ci.cancel();
            HudCapture.storeApplied(id, HudCustomState.getX(id), HudCustomState.getY(id));
            self.method_25302(texture, x + transform[0], y + transform[1], u, v, transform[2], transform[3]);
         } finally {
            mappet$reentrant = false;
         }
      }
   }

   @Inject(method = "method_25290", at = @At("HEAD"), cancellable = true)
   private void mappet$captureDrawTextureFloat(class_2960 texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, CallbackInfo ci) {
      if (mappet$reentrant) {
         return;
      }

      class_332 self = (class_332)(Object)this;
      String id = HudCapture.drawAndKey(self, texture, x, y, width, height, false);
      if (id == null) {
         return;
      }

      if (HudCustomState.hasTransform(id) && !HudCustomState.isVisible(id)) {
         mappet$cancel(ci);
         return;
      }

      int[] transform = mappet$offsetScale(id, width, height);
      if (transform != null) {
         mappet$reentrant = true;

         try {
            ci.cancel();
            HudCapture.storeApplied(id, HudCustomState.getX(id), HudCustomState.getY(id));
            self.method_25290(texture, x + transform[0], y + transform[1], u, v, transform[2], transform[3], textureWidth, textureHeight);
         } finally {
            mappet$reentrant = false;
         }
      }
   }

   @Inject(method = "method_25298", at = @At("HEAD"))
   private void mappet$captureDrawSprite(int x, int y, int z, int width, int height, class_1058 sprite, CallbackInfo ci) {
      if (!mappet$reentrant) {
         HudCapture.captureSprite((class_332)(Object)this, sprite.method_45852(), x, y, width, height);
      }
   }
}