package mchorse.mappet.mixins;

import mchorse.mappet.client.FirstPersonBodyRenderer;
import net.minecraft.class_1007;
import net.minecraft.class_742;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_1007.class)
public abstract class PlayerEntityRendererHeadMixin {
   @Unique
   private boolean mappet$headWasVisible;

   @Inject(method = "method_4215", at = @At("HEAD"))
   private void mappet$hideHead(class_742 entity, float yaw, float tickDelta, class_4587 matrices, class_4597 consumers, int light, CallbackInfo ci) {
      if (FirstPersonBodyRenderer.isRendering()) {
         class_591<?> model = ((class_1007)(Object)this).method_4038();
         this.mappet$headWasVisible = model.field_3484.field_3665;
         model.field_3484.field_3665 = false;
      }
   }

   @Inject(method = "method_4215", at = @At("TAIL"))
   private void mappet$restoreHead(class_742 entity, float yaw, float tickDelta, class_4587 matrices, class_4597 consumers, int light, CallbackInfo ci) {
      if (FirstPersonBodyRenderer.isRendering()) {
         class_591<?> model = ((class_1007)(Object)this).method_4038();
         model.field_3484.field_3665 = this.mappet$headWasVisible;
      }
   }
}
