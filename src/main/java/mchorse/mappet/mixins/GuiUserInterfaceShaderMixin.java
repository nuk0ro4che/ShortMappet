package mchorse.mappet.mixins;

import mchorse.mappet.client.gui.GuiUserInterface;
import mchorse.mappet.client.shaders.ClientShaderRuntime;
import net.minecraft.class_332;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GuiUserInterface.class)
public abstract class GuiUserInterfaceShaderMixin {
   @Inject(method = "method_25394", at = @At("HEAD"))
   private void mappet$beginInterfaceShader(class_332 context, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
      ClientShaderRuntime.beginInterface();
   }

   @Inject(method = "method_25394", at = @At("RETURN"))
   private void mappet$endInterfaceShader(class_332 context, int mouseX, int mouseY, float tickDelta, CallbackInfo ci) {
      ClientShaderRuntime.endInterface(tickDelta);
   }
}
