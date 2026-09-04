package mchorse.mappet.mixins;

import mchorse.mappet.client.InputTriggerHandler;
import mchorse.mappet.client.ClientMovementLockState;
import net.minecraft.class_309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_309.class})
public abstract class KeyboardInputMixin {
   @Inject(
      method = {"method_1466"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$keyboardInput(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
      if (ClientMovementLockState.shouldBlockKey(key, scancode)) {
         ci.cancel();
         return;
      }

      InputTriggerHandler.onKeyboard(key, action);
   }
}
