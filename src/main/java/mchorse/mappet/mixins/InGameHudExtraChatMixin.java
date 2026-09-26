package mchorse.mappet.mixins;

import mchorse.mappet.client.HudCapture;
import mchorse.mappet.client.HudVisibilityState;
import net.minecraft.class_332;
import net.minecraft.class_338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_338.class)
public abstract class InGameHudExtraChatMixin {
   @Inject(method = "method_1805", at = @At("HEAD"))
   private void mappet$captureChatHead(class_332 context, int x, int y, int z, CallbackInfo ci) {
      HudCapture.beginElement(HudVisibilityState.Element.CHAT);
   }

   @Inject(method = "method_1805", at = @At("RETURN"))
   private void mappet$captureChatReturn(class_332 context, int x, int y, int z, CallbackInfo ci) {
      HudCapture.endElement();
   }
}