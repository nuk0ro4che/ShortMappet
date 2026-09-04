package mchorse.mappet.mixins;

import mchorse.aperture.ClientProxy;
import mchorse.aperture.camera.data.Position;
import mchorse.aperture.client.ApertureClient;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_5498;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = class_4184.class, priority = 900)
public abstract class UnderwaterCameraMixin {
   @Redirect(
      method = "method_19321",
      at = @At(
         value = "INVOKE",
         target = "Lmchorse/aperture/client/ApertureClient;frame(F)Lmchorse/aperture/camera/data/Position;"
      ),
      require = 0
   )
   private Position mappet$useVanillaCameraPositionUnderwater(float tickDelta) {
      class_310 client = class_310.method_1551();
      class_746 player = client.field_1724;
      boolean vanillaFirstPerson = client.field_1690.method_31044() == class_5498.field_26664;

      if (player != null && vanillaFirstPerson && player.method_5869() && !ClientProxy.runner.isRunning()) {
         return null;
      }

      return ApertureClient.frame(tickDelta);
   }
}
