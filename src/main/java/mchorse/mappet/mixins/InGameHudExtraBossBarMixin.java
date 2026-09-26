package mchorse.mappet.mixins;

import mchorse.mappet.client.HudCapture;
import mchorse.mappet.client.HudVisibilityState;
import net.minecraft.class_332;
import net.minecraft.class_359;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_359.class)
public abstract class InGameHudExtraBossBarMixin {
   @Inject(method = "method_1957", at = @At("HEAD"))
   private void mappet$captureBossBarHead(class_332 context, CallbackInfo ci) {
      HudCapture.beginElement(HudVisibilityState.Element.BOSS_BAR);
   }

   @Inject(method = "method_1957", at = @At("RETURN"))
   private void mappet$captureBossBarReturn(class_332 context, CallbackInfo ci) {
      HudCapture.endElement();
   }
}