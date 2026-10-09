package mchorse.mappet.mixins;

import mchorse.mappet.client.WorldLoadCancelState;
import net.minecraft.class_1132;
import net.minecraft.class_310;
import net.minecraft.class_32;
import net.minecraft.class_3283;
import net.minecraft.class_442;
import net.minecraft.class_6904;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels entering a singleplayer world before the client connects to the
 * integrated server.
 *
 * <p>{@code Minecraft.method_29610} first stops the previous level, starts the
 * integrated server, then blocks the render thread in a loop until the server
 * is ready (see {@code MinecraftServerReadyMixin}) and finally connects the
 * client to it. When the player cancels the loading screen we want to abort
 * right after that blocking loop but before the connection is made, otherwise
 * the client either connects to a server that is shutting down or stays stuck
 * on the loading screen forever (which also breaks the \"Quit Game\" button).
 * Fully stopping the server here also releases the world files so that loading
 * a world afterwards does not crash.</p>
 */
@Mixin(class_310.class)
public abstract class MinecraftWorldLoadMixin
{
    @Inject(
        method = "method_29610",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1132;method_3787()Lnet/minecraft/class_3242;"),
        cancellable = true
    )
    private void mappet$cancelWorldLoad(String levelName, class_32.class_5143 access, class_3283 packs, class_6904 worldStem, boolean isNewWorld, CallbackInfo ci)
    {
        if (!WorldLoadCancelState.consume())
        {
            return;
        }

        class_310 client = class_310.method_1551();
        class_1132 server = client.method_1576();

        if (server != null && !server.method_16043())
        {
            /* Fully stop the integrated server so that the world storage is
             * unlocked and the next world can be loaded safely. */
            server.method_3747(true);
        }

        client.method_1507(new class_442());
        ci.cancel();
    }
}
