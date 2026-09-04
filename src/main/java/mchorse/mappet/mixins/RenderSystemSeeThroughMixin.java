package mchorse.mappet.mixins;

import com.mojang.blaze3d.systems.RenderSystem;

import mchorse.mappet.client.RenderingHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public abstract class RenderSystemSeeThroughMixin
{
    @Inject(method = "enableDepthTest", at = @At("HEAD"), cancellable = true)
    private static void mappet$keepDepthDisabled(CallbackInfo ci)
    {
        if (RenderingHandler.renderingSeeThrough)
        {
            ci.cancel();
        }
    }
}
