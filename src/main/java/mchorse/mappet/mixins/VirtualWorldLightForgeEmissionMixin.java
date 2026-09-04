package mchorse.mappet.mixins;

import mchorse.mappet.api.scripts.lights.VanillaWorldLightManager;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;






@Pseudo
@Mixin(targets = "net.minecraftforge.common.extensions.IForgeBlockState", remap = false)
public interface VirtualWorldLightForgeEmissionMixin {
   @Inject(method = "getLightEmission", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
   private void mappet$virtualForgeLightEmission(class_1922 world, class_2338 position, CallbackInfoReturnable<Integer> ci) {
      int virtualEmission = VanillaWorldLightManager.getVirtualEmission(world, position);
      if (virtualEmission > ci.getReturnValueI()) {
         ci.setReturnValue(virtualEmission);
      }
   }
}
