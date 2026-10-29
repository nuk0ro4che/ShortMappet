package mchorse.mappet.mixins;

import java.util.List;
import mchorse.mappet.MappetClient;
import net.minecraft.class_3262;
import net.minecraft.class_3283;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes sure that the "Mappet custom sounds" resource pack is registered and
 * enabled before the list of the packs to load is built.
 *
 * <p>The game builds that list twice while it starts up: once for the options
 * and once for the resource reload it performs on startup. Both are done from
 * the {@code MinecraftClient} constructor, which is where the mod entrypoints
 * are invoked as well. Registering the pack here makes sure that it is a part
 * of the very first reload, so that Mappet doesn't have to force an extra
 * reload of all the resources afterwards.</p>
 *
 * <p>That extra reload is not harmless: it frees the block atlas texture
 * while VulkanMod still holds a pending layout transition for it, which makes
 * the graphics driver crash with a SIGSEGV as soon as the transition is
 * submitted (on world join).</p>
 */
@Mixin(class_3283.class)
public abstract class ResourcePackManagerMixin
{
    @Inject(method = "method_29211", at = @At("HEAD"))
    private void mappet$registerSoundPack(CallbackInfoReturnable<List<class_3262>> ci)
    {
        MappetClient.registerSoundPack((class_3283)(Object)this);
    }
}
