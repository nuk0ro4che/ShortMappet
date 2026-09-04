package mchorse.mappet.mixins;

import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.class_2487;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;









@Mixin(value = EntityMorph.class, remap = false)
public abstract class EntityMorphSlimPersistenceMixin
{
    @Unique
    private boolean mappet$preserveSlimDuringProfileLoad;

    @Shadow
    private void resolveProfileAsync(boolean seedSlim)
    {
    }

    @Inject(method = "fromNBT(Lnet/minecraft/class_2487;)V", at = @At("HEAD"), remap = false, require = 1)
    private void mappet$rememberSavedSlim(class_2487 tag, CallbackInfo ci)
    {
        this.mappet$preserveSlimDuringProfileLoad = tag.method_10545("Slim");
    }

    @Inject(method = "fromNBT(Lnet/minecraft/class_2487;)V", at = @At("RETURN"), remap = false, require = 1)
    private void mappet$clearSavedSlimMarker(class_2487 tag, CallbackInfo ci)
    {
        this.mappet$preserveSlimDuringProfileLoad = false;
    }

    



    @Overwrite
    public void setProfile(String username)
    {
        EntityMorph morph = (EntityMorph)(Object)this;

        morph.profile = EntityMorph.offlineProfile(username);
        this.resolveProfileAsync(!this.mappet$preserveSlimDuringProfileLoad);
    }
}
