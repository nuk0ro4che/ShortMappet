package mchorse.mappet.mixins;

import mchorse.mappet.client.ClientFirstPersonLight;
import mchorse.mappet.client.HandMorphRenderer;
import mchorse.mappet.hand.HandState;
import mchorse.mappet.hand.Hands;
import net.minecraft.class_1268;
import net.minecraft.class_1306;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;
import net.minecraft.class_759;
import net.minecraft.class_7833;
import net.minecraft.class_811;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_759.class})
public abstract class HeldItemRendererMixin {
   @Shadow
   private void method_3222(class_4587 matrices, class_4597 consumers, int light, float equipProgress, class_1306 arm, float swingProgress, class_1799 stack) {
   }

   @Unique
   private HandState.Side mappet$currentHand;

   @Inject(method = {"method_3228"}, at = {@At("HEAD")}, cancellable = true)
   private void mappet$transformFirstPersonHand(class_742 player, float tickDelta, float pitch, class_1268 hand, float swingProgress, class_1799 stack, float equipProgress, class_4587 matrices, class_4597 consumers, int light, CallbackInfo ci) {
      ClientFirstPersonLight.capture(light);
      HandState.Side state = Hands.get(player).get(hand == class_1268.field_5808 ? 0 : 1);
      state.update(System.currentTimeMillis());
      this.mappet$currentHand = state;

      if (state.morph != null) {
         HandMorphRenderer.render(state.morph, player, hand, tickDelta, matrices, consumers, light, state);
         ci.cancel();
         return;
      }

      



      if (HandMorphRenderer.hasActivePlayerEntityMorph(player)) {
         if (hand != class_1268.field_5808) {
            if (stack.method_7960()) {
               ci.cancel();
            }
            return;
         }

         if (!stack.method_7960()) {
            return;
         }

         if (HandMorphRenderer.renderActivePlayerEntityMorph(player, hand, tickDelta, swingProgress, equipProgress, matrices, consumers, light, state)) {
            ci.cancel();
            return;
         }
      }

      if (!state.renderArm && !state.renderItem) {
         ci.cancel();
         return;
      }

      double sign = hand == class_1268.field_5808 ? 1.0D : -1.0D;
      matrices.method_22904(state.x * sign, state.y, state.z);
      matrices.method_22907(class_7833.field_40713.rotationDegrees((float)state.pitch));
      matrices.method_22907(class_7833.field_40714.rotationDegrees((float)(state.yaw * sign)));
      matrices.method_22907(class_7833.field_40715.rotationDegrees((float)(state.roll * sign)));
   }

   @Redirect(method = {"method_3228"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_759;method_3222(Lnet/minecraft/class_4587;Lnet/minecraft/class_4597;IFLnet/minecraft/class_1306;FLnet/minecraft/class_1799;)V"))
   private void mappet$renderArm(class_759 renderer, class_4587 matrices, class_4597 consumers, int light, float equipProgress, class_1306 arm, float swingProgress, class_1799 stack) {
      if (this.mappet$currentHand == null || this.mappet$currentHand.renderArm) {
         this.method_3222(matrices, consumers, light, equipProgress, arm, swingProgress, stack);
      }
   }

   @Redirect(method = {"method_3228"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/class_759;method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;ZLnet/minecraft/class_4587;Lnet/minecraft/class_4597;I)V"))
   private void mappet$renderItem(class_759 renderer, class_1309 entity, class_1799 stack, class_811 transform, boolean leftHanded, class_4587 matrices, class_4597 consumers, int light) {
      if (this.mappet$currentHand == null || this.mappet$currentHand.renderItem) {
         renderer.method_3233(entity, stack, transform, leftHanded, matrices, consumers, light);
      }
   }
}
