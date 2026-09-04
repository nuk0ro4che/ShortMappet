package mchorse.mappet.mixins;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_1676;
import net.minecraft.class_239;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1676.class})
public class ProjectileEventMixin {
   @Inject(
      method = {"method_7488"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$impact(class_239 result, CallbackInfo ci) {
      class_1676 projectile = (class_1676)(Object)this;
      if (!projectile.method_37908().field_9236) {
         LegacyEvents.ProjectileImpactEvent event = new LegacyEvents.ProjectileImpactEvent(projectile, result);
         CommonProxy.eventHandler.onProjectileImpact(event);
         if (event.isCanceled()) {
            ci.cancel();
         }

      }
   }
}
