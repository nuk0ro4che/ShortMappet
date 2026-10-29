package mchorse.mappet.mixins;

import java.util.List;
import mchorse.mappet.MappetClient;
import net.minecraft.class_3262;
import net.minecraft.class_6360;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps track of whether the "Mappet custom sounds" resource pack was a part
 * of the last resource reload.
 *
 * <p>Mappet uses this to know whether the custom sounds are actually loaded.
 * If they aren't, the pack was registered too late and the resources have to
 * be reloaded once more (see {@code MappetClient}).</p>
 */
@Mixin(class_6360.class)
public abstract class ResourceManagerReloadMixin
{
    @Inject(method = "method_36563", at = @At("HEAD"))
    private void mappet$trackSoundPack(class_6360.class_6362 listener, List<class_3262> packs, CallbackInfo ci)
    {
        MappetClient.onResourceReload(packs);
    }
}
