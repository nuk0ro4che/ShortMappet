package mchorse.mappet.mixins;

import mchorse.mappet.client.ClientBlockInteractHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_636.class})
public abstract class ClientBlockInteractionMixin {
   @Shadow
   @Final
   private class_310 field_3712;

   @Inject(
      method = {"method_2896"},
      at = {@At("HEAD")}
   )
   private void mappet$interactBlock(class_746 player, class_1268 hand, class_3965 hitResult, CallbackInfoReturnable<class_1269> cir) {
      if (this.field_3712.field_1687 != null && hitResult != null) {
         class_2338 pos = hitResult.method_17777();
         class_2680 state = this.field_3712.field_1687.method_8320(pos);

         ClientBlockInteractHandler.onRightClickBlock(pos, state, hand);
      }

   }

   @Inject(
      method = {"method_2910"},
      at = {@At("HEAD")}
   )
   private void mappet$attackBlock(class_2338 pos, class_2350 direction, CallbackInfoReturnable<Boolean> cir) {
      if (this.field_3712.field_1724 != null && this.field_3712.field_1687 != null) {
         class_2680 state = this.field_3712.field_1687.method_8320(pos);

         ClientBlockInteractHandler.onLeftClickBlock(pos, state);
      }

   }
}
