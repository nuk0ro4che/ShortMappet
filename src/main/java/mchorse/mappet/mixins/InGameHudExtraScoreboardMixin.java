package mchorse.mappet.mixins;

import mchorse.mappet.client.HudCapture;
import mchorse.mappet.client.HudVisibilityState;
import net.minecraft.class_332;
import net.minecraft.class_365;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_365.class)
public abstract class InGameHudExtraScoreboardMixin {
   @Inject(method = "method_1978", at = @At("HEAD"))
   private void mappet$captureScoreboardHead(class_332 context, CallbackInfo ci) {
      HudCapture.beginElement(HudVisibilityState.Element.SCOREBOARD);
   }

   @Inject(method = "method_1978", at = @At("RETURN"))
   private void mappet$captureScoreboardReturn(class_332 context, CallbackInfo ci) {
      HudCapture.endElement();
   }

   @Inject(method = "method_1979", at = @At("HEAD"))
   private void mappet$captureScoreboard2Head(class_332 context, CallbackInfo ci) {
      HudCapture.beginElement(HudVisibilityState.Element.SCOREBOARD);
   }

   @Inject(method = "method_1979", at = @At("RETURN"))
   private void mappet$captureScoreboard2Return(class_332 context, CallbackInfo ci) {
      HudCapture.endElement();
   }
}