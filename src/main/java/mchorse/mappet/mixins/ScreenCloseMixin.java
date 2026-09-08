package mchorse.mappet.mixins;

import mchorse.mappet.client.KeyboardHandler;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_437.class)
public abstract class ScreenCloseMixin {
   @Inject(
      method = {"method_25419"},
      at = {@At("HEAD")}
   )
   private void mappet$screenClose(CallbackInfo ci) {
      KeyboardHandler.onScreenClose((class_437)(Object)this);
   }
}