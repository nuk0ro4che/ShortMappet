package mchorse.mappet.mixins;

import mchorse.mappet.client.WorldLoadCancelState;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@code MinecraftServer.method_3820} is only used by
 * {@code Minecraft.method_29610} as the condition of the blocking
 * "wait for the integrated server to start" loop. Returning {@code true} while
 * a cancel is requested lets that loop finish immediately instead of hanging
 * on a server that is being stopped.
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerReadyMixin
{
    @Inject(method = "method_3820", at = @At("HEAD"), cancellable = true)
    private void mappet$exitWaitLoopOnCancel(CallbackInfoReturnable<Boolean> cir)
    {
        if (WorldLoadCancelState.isRequested())
        {
            cir.setReturnValue(true);
        }
    }
}
