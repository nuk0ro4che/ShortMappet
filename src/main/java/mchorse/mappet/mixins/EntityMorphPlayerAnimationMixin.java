package mchorse.mappet.mixins;

import mchorse.metamorph.api.morphs.EntityMorph;
import mchorse.metamorph.client.render.EntityMorphRenderer;
import mchorse.metamorph.client.render.MorphRenderContext;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;






@Mixin(value = EntityMorphRenderer.class, remap = false)
public abstract class EntityMorphPlayerAnimationMixin
{
    @Inject(
        method = "render(Lmchorse/metamorph/api/morphs/EntityMorph;Lnet/minecraft/class_1309;DDDFFLmchorse/metamorph/client/render/MorphRenderContext;)V",
        at = @At(
            value = "INVOKE",
            target = "Lmchorse/metamorph/client/render/EntityMorphRenderer;copyRotations(Lnet/minecraft/class_1309;Lnet/minecraft/class_1309;)V",
            shift = At.Shift.AFTER,
            remap = false
        ),
        remap = false,
        require = 1
    )
    private void mappet$copyPlayerAnimationState(EntityMorph morph, class_1309 entity,
        double x, double y, double z, float entityYaw, float partialTicks,
        MorphRenderContext context, CallbackInfo ci)
    {
        if (!morph.isPlayer())
        {
            return;
        }

        class_1309 dummy = morph.getEntity(entity.method_37908());

        if (dummy == null)
        {
            return;
        }

        LimbAnimatorAccessor sourceAnimator = (LimbAnimatorAccessor)(Object)entity.field_42108;
        LimbAnimatorAccessor dummyAnimator = (LimbAnimatorAccessor)(Object)dummy.field_42108;

        dummyAnimator.mappet$setPreviousSpeed(sourceAnimator.mappet$getPreviousSpeed());
        dummyAnimator.mappet$setSpeed(sourceAnimator.mappet$getSpeed());
        dummyAnimator.mappet$setPosition(sourceAnimator.mappet$getPosition());

        dummy.field_6252 = entity.field_6252;
        dummy.field_6279 = entity.field_6279;
        dummy.field_6229 = entity.field_6229;
        dummy.field_6251 = entity.field_6251;
        dummy.field_6012 = entity.field_6012;

        



        if (entity instanceof class_1657 sourcePlayer && dummy instanceof class_1657 dummyPlayer)
        {
            dummyPlayer.method_7283(sourcePlayer.method_6068());
        }
    }
}
