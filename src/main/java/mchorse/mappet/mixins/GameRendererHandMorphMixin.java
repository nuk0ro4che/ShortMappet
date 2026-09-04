package mchorse.mappet.mixins;

import mchorse.mappet.client.HandMorphRenderer;
import mchorse.mappet.client.shaders.ClientShaderRuntime;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_757.class)
public abstract class GameRendererHandMorphMixin
{
    @Inject(
        method = "method_3198(Lnet/minecraft/class_4587;F)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void mappet$disableViewBobbingForHandMorph(class_4587 matrices, float tickDelta, CallbackInfo ci)
    {
        class_310 client = class_310.method_1551();

        if (HandMorphRenderer.hasActiveMorph(client.field_1724))
        {
            ci.cancel();
        }
    }

    @Inject(
        method = "method_3172(Lnet/minecraft/class_4587;Lnet/minecraft/class_4184;F)V",
        at = @At("HEAD")
    )
    private void mappet$renderShaderBeforeHand(class_4587 matrices, class_4184 camera, float tickDelta, CallbackInfo ci)
    {
        

        ClientShaderRuntime.render(tickDelta);
        ClientShaderRuntime.beginHand();
    }

    @Inject(
        method = "method_3172(Lnet/minecraft/class_4587;Lnet/minecraft/class_4184;F)V",
        at = @At("RETURN")
    )
    private void mappet$renderShaderAfterHand(class_4587 matrices, class_4184 camera, float tickDelta, CallbackInfo ci)
    {
        ClientShaderRuntime.endHand(tickDelta);
    }
}
