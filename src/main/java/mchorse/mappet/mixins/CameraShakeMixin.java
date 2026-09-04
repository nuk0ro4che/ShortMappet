package mchorse.mappet.mixins;

import mchorse.mappet.client.CameraShakeHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_4184;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_4184.class)
public abstract class CameraShakeMixin {
   @Shadow
   private float field_18717;

   @Shadow
   private float field_18718;

   @Shadow
   @Final
   private Quaternionf field_21518;

   @Invoker("method_19325")
   public abstract void mappet$setRotation(float yaw, float pitch);

   @Inject(method = "method_19321", at = @At("TAIL"))
   private void mappet$applyCameraShake(class_1922 world, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
      float pitch = CameraShakeHandler.getPitch();
      float yaw = CameraShakeHandler.getYaw();
      float roll = CameraShakeHandler.getRoll();

      if (pitch == 0.0F && yaw == 0.0F && roll == 0.0F) {
         return;
      }

      float cameraPitch = Math.max(-89.9F, Math.min(89.9F, this.field_18717 + pitch));
      float cameraYaw = this.field_18718 + yaw;
      this.mappet$setRotation(cameraYaw, cameraPitch);

      if (roll != 0.0F) {
         roll = Math.max(-45.0F, Math.min(45.0F, roll));
         this.field_21518.rotateZ(roll * 0.017453292F);
      }
   }
}
