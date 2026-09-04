package mchorse.mappet.mixins;

import mchorse.mappet.client.HandMorphRenderer;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMorph.class)
public abstract class AbstractMorphMixin {
   @Inject(method = "updateHitbox", at = @At("HEAD"), cancellable = true)
   private void mappet$skipHandMorphHitbox(class_1309 entity, CallbackInfo ci) {
      if (HandMorphRenderer.isUpdatingHandMorph()) {
         ci.cancel();
      }

   }
}
