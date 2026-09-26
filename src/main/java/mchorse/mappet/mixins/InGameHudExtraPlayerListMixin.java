package mchorse.mappet.mixins;

import mchorse.mappet.client.HudCapture;
import mchorse.mappet.client.HudVisibilityState;
import net.minecraft.class_332;
import net.minecraft.class_340;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_340.class)
public abstract class InGameHudExtraPlayerListMixin {
   @Inject(method = "method_1846", at = @At("HEAD"))
   private void mappet$capturePlayerListHead(class_332 context, CallbackInfo ci) {
      HudCapture.beginElement(HudVisibilityState.Element.PLAYER_LIST);
   }

   @Inject(method = "method_1846", at = @At("RETURN"))
   private void mappet$capturePlayerListReturn(class_332 context, CallbackInfo ci) {
      HudCapture.endElement();
   }

   @Inject(method = "method_1847", at = @At("HEAD"))
   private void mappet$capturePlayerListBgHead(class_332 context, CallbackInfo ci) {
      HudCapture.beginElement(HudVisibilityState.Element.PLAYER_LIST);
   }

   @Inject(method = "method_1847", at = @At("RETURN"))
   private void mappet$capturePlayerListBgReturn(class_332 context, CallbackInfo ci) {
      HudCapture.endElement();
   }
}