package mchorse.mappet.mixins;

import mchorse.mappet.client.HudCapture;
import mchorse.mappet.client.HudVisibilityState;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.client.shaders.ClientShaderRuntime;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2960;
import net.minecraft.class_329;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_329.class)
public abstract class InGameHudVisibilityMixin {
   private static boolean mappet$pushed;

   @Inject(method = "method_1753", at = @At("HEAD"))
   private void mappet$renderHudBeforeVanilla(class_332 context, float tickDelta, CallbackInfo ci) {
      ClientShaderRuntime.beginHud();
      RenderingHandler.renderHud(tickDelta);
      HudCapture.begin();
   }

   @Inject(method = "method_1753", at = @At("RETURN"))
   private void mappet$renderHudAfterVanilla(class_332 context, float tickDelta, CallbackInfo ci) {
      HudCapture.finish();
      RenderingHandler.renderHudPicker(context, tickDelta);
      ClientShaderRuntime.endHud(tickDelta);
   }

   private static void mappet$begin(class_332 context, HudVisibilityState.Element element, boolean visible) {
      HudCapture.beginElement(element);
      mappet$pushed = false;

      if (visible) {
         int x = HudVisibilityState.getX(element);
         int y = HudVisibilityState.getY(element);
         if (x != 0 || y != 0) {
            context.method_51448().method_22903();
            context.method_51448().method_22904(x, y, 0.0D);
            mappet$pushed = true;
         }
      }
   }

   private static void mappet$end(class_332 context, HudVisibilityState.Element element) {
      if (mappet$pushed) {
         context.method_51448().method_22909();
         mappet$pushed = false;
      }

      HudCapture.endElement();
   }

   @Inject(method = "method_1759", at = @At("HEAD"), cancellable = true)
   private void mappet$hideHotbar(float tickDelta, class_332 context, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.HOTBAR);
      mappet$begin(context, HudVisibilityState.Element.HOTBAR, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.HOTBAR);
         ci.cancel();
      }
   }

   @Inject(method = "method_1736", at = @At("HEAD"), cancellable = true)
   private void mappet$hideCrosshair(class_332 context, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.CROSSHAIR);
      mappet$begin(context, HudVisibilityState.Element.CROSSHAIR, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.CROSSHAIR);
         ci.cancel();
      }
   }

   @Inject(method = "method_1760", at = @At("HEAD"), cancellable = true)
   private void mappet$hideStatusBars(class_332 context, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.HEALTH)
         || HudVisibilityState.isVisible(HudVisibilityState.Element.HUNGER)
         || HudVisibilityState.isVisible(HudVisibilityState.Element.EXPERIENCE)
         || HudVisibilityState.isVisible(HudVisibilityState.Element.MOUNT_HEALTH);
      mappet$begin(context, HudVisibilityState.Element.HEALTH, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.HEALTH);
         ci.cancel();
      }
   }

   @Inject(method = "method_37298", at = @At("HEAD"), cancellable = true)
   private void mappet$hideHealthBar(class_332 context, class_1657 player, int x, int y, int lines, int regeneration, float maxHealth, int absorption, int health, int lastHealth, boolean hardcore, CallbackInfo ci) {
      HudCapture.beginElement(HudVisibilityState.Element.HEALTH);
      if (!HudVisibilityState.isVisible(HudVisibilityState.Element.HEALTH)) {
         HudCapture.endElement();
         ci.cancel();
      }
   }

   @Redirect(method = "method_1760", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_332;method_25302(Lnet/minecraft/class_2960;IIIIII)V", ordinal = 3))
   private void mappet$hideHunger0(class_332 context, class_2960 texture, int x, int y, int u, int v, int width, int height) {
      HudCapture.beginElement(HudVisibilityState.Element.HUNGER);
      if (HudVisibilityState.isVisible(HudVisibilityState.Element.HUNGER)) {
         context.method_25302(texture, x, y, u, v, width, height);
      }
      HudCapture.endElement();
   }

   @Redirect(method = "method_1760", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_332;method_25302(Lnet/minecraft/class_2960;IIIIII)V", ordinal = 4))
   private void mappet$hideHunger1(class_332 context, class_2960 texture, int x, int y, int u, int v, int width, int height) {
      HudCapture.beginElement(HudVisibilityState.Element.HUNGER);
      if (HudVisibilityState.isVisible(HudVisibilityState.Element.HUNGER)) {
         context.method_25302(texture, x, y, u, v, width, height);
      }
      HudCapture.endElement();
   }

   @Redirect(method = "method_1760", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_332;method_25302(Lnet/minecraft/class_2960;IIIIII)V", ordinal = 5))
   private void mappet$hideHunger2(class_332 context, class_2960 texture, int x, int y, int u, int v, int width, int height) {
      HudCapture.beginElement(HudVisibilityState.Element.HUNGER);
      if (HudVisibilityState.isVisible(HudVisibilityState.Element.HUNGER)) {
         context.method_25302(texture, x, y, u, v, width, height);
      }
      HudCapture.endElement();
   }

   @Inject(method = "method_1754", at = @At("HEAD"), cancellable = true)
   private void mappet$hideExperience(class_332 context, int x, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.EXPERIENCE);
      mappet$begin(context, HudVisibilityState.Element.EXPERIENCE, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.EXPERIENCE);
         ci.cancel();
      }
   }

   @Inject(method = "method_1765", at = @At("HEAD"), cancellable = true)
   private void mappet$hideStatusEffects(class_332 context, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.STATUS_EFFECTS);
      mappet$begin(context, HudVisibilityState.Element.STATUS_EFFECTS, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.STATUS_EFFECTS);
         ci.cancel();
      }
   }

   @Inject(method = "method_1741", at = @At("HEAD"), cancellable = true)
   private void mappet$hideMountHealth(class_332 context, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.MOUNT_HEALTH);
      mappet$begin(context, HudVisibilityState.Element.MOUNT_HEALTH, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.MOUNT_HEALTH);
         ci.cancel();
      }
   }

   @Inject(method = "method_1735", at = @At("HEAD"), cancellable = true)
   private void mappet$hideVignette(class_332 context, class_1297 entity, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.VIGNETTE);
      mappet$begin(context, HudVisibilityState.Element.VIGNETTE, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.VIGNETTE);
         ci.cancel();
      }
   }

   @Inject(method = "method_32598", at = @At("HEAD"), cancellable = true)
   private void mappet$hideSpyglass(class_332 context, float scale, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.SPYGLASS);
      mappet$begin(context, HudVisibilityState.Element.SPYGLASS, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.SPYGLASS);
         ci.cancel();
      }
   }

   @Inject(method = "method_1749", at = @At("HEAD"), cancellable = true)
   private void mappet$hideItemTooltip(class_332 context, CallbackInfo ci) {
      boolean visible = HudVisibilityState.isVisible(HudVisibilityState.Element.ITEM_TOOLTIP);
      mappet$begin(context, HudVisibilityState.Element.ITEM_TOOLTIP, visible);
      if (!visible) {
         mappet$end(context, HudVisibilityState.Element.ITEM_TOOLTIP);
         ci.cancel();
      }
   }

   @Inject(method = "method_1759", at = @At("RETURN"))
   private void mappet$popHotbar(float tickDelta, class_332 context, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.HOTBAR);
   }

   @Inject(method = "method_1736", at = @At("RETURN"))
   private void mappet$popCrosshair(class_332 context, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.CROSSHAIR);
   }

   @Inject(method = "method_1760", at = @At("RETURN"))
   private void mappet$popStatusBars(class_332 context, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.HEALTH);
   }

   @Inject(method = "method_1754", at = @At("RETURN"))
   private void mappet$popExperience(class_332 context, int x, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.EXPERIENCE);
   }

   @Inject(method = "method_1765", at = @At("RETURN"))
   private void mappet$popStatusEffects(class_332 context, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.STATUS_EFFECTS);
   }

   @Inject(method = "method_1741", at = @At("RETURN"))
   private void mappet$popMountHealth(class_332 context, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.MOUNT_HEALTH);
   }

   @Inject(method = "method_1735", at = @At("RETURN"))
   private void mappet$popVignette(class_332 context, class_1297 entity, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.VIGNETTE);
   }

   @Inject(method = "method_32598", at = @At("RETURN"))
   private void mappet$popSpyglass(class_332 context, float scale, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.SPYGLASS);
   }

   @Inject(method = "method_1749", at = @At("RETURN"))
   private void mappet$popItemTooltip(class_332 context, CallbackInfo ci) {
      mappet$end(context, HudVisibilityState.Element.ITEM_TOOLTIP);
   }
}