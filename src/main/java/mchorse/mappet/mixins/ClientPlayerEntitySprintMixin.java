package mchorse.mappet.mixins;

import mchorse.mappet.client.ClientSprintHungerState;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla refuses to start sprinting when the food level is six or lower.
 * When the script asks for it, allow sprinting anyway.
 */
@Mixin({class_746.class})
public abstract class ClientPlayerEntitySprintMixin {
   @Inject(method = {"method_46743"}, at = {@At("HEAD")}, cancellable = true)
   private void mappet$allowSprintWhileHungry(CallbackInfoReturnable<Boolean> cir) {
      if (ClientSprintHungerState.isDisabled()) {
         cir.setReturnValue(true);
      }
   }
}
