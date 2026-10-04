package mchorse.mappet.mixins;

import mchorse.mappet.client.HudCapture;
import net.minecraft.class_757;
import net.minecraft.class_329;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * The whole GUI phase of a frame lives inside {@link class_329#method_1753},
 * because Forge replaces the vanilla HUD with its own subclass that fires
 * {@code RenderGuiEvent.Pre} and {@code RenderGuiEvent.Post} around it. Opening
 * the capture right before that call (and closing right after it) covers every
 * way a mod can draw a HUD, while world geometry is still excluded.
 */
@Mixin(class_757.class)
public abstract class GameRendererHudCaptureMixin {
   @Inject(method = "method_3192", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_329;method_1753(Lnet/minecraft/class_332;F)V", shift = At.Shift.BEFORE))
   private void mappet$beforeHud(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
      HudCapture.begin();
   }

   @Inject(method = "method_3192", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_329;method_1753(Lnet/minecraft/class_332;F)V", shift = At.Shift.AFTER))
   private void mappet$afterHud(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
      if (HudCapture.isCapturing()) {
         HudCapture.finish();
      }
   }
}