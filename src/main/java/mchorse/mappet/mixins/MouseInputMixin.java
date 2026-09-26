package mchorse.mappet.mixins;

import mchorse.mappet.client.InputTriggerHandler;
import mchorse.mappet.client.ClientMousePositionController;
import mchorse.mappet.client.ClientMovementLockState;
import mchorse.mappet.client.HudIdPicker;
import net.minecraft.class_310;
import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_312.class})
public abstract class MouseInputMixin {
   @Inject(
      method = {"method_1600"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$cursorPosition(long window, double x, double y, CallbackInfo ci) {
      if (ClientMousePositionController.consumeSyntheticCursorEvent()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"method_1606"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$lockCamera(CallbackInfo ci) {
      if (HudIdPicker.isActive()) {
         class_310 client = class_310.method_1551();
         if (client == null || client.field_1755 == null) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"method_1601"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$mouseInput(long window, int button, int action, int modifiers, CallbackInfo ci) {
      if (ClientMovementLockState.shouldBlockMouse(button)) {
         ci.cancel();
         return;
      }

      if (HudIdPicker.isActive()) {
         ci.cancel();
         if (button == 0 || button == 1) {
            if (action == 1) {
               HudIdPicker.mouseClicked(button);
            }
         }
         return;
      }

      InputTriggerHandler.onMouse(button, action);
   }

   @Inject(
      method = {"method_1598"},
      at = {@At("HEAD")}
   )
   private void mappet$mouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
      InputTriggerHandler.onScroll(vertical);
   }
}
