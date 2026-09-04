package mchorse.mappet.mixins;

import mchorse.mappet.client.HandMorphRenderer;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.api.morphs.EntityMorph;
import net.minecraft.class_1309;
import net.minecraft.class_2960;
import net.minecraft.class_897;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "mchorse.metamorph.client.render.EntityMorphRenderer")
public abstract class EntityMorphRendererMixin
{
    @Redirect(
        method = "renderHand(Lmchorse/metamorph/api/morphs/EntityMorph;Lnet/minecraft/class_1657;Lnet/minecraft/class_1268;)Z",
        at = @At(value = "INVOKE", target = "Lmchorse/metamorph/client/render/EntityMorphRenderer;mobTexture(Lnet/minecraft/class_898;Lnet/minecraft/class_1309;)Lnet/minecraft/class_2960;"))
    private class_2960 mappet$useFredDefaultTexture(class_898 renderer, class_1309 entity)
    {
        AbstractMorph morph = HandMorphRenderer.getCurrentMorph();

        if (morph instanceof EntityMorph entityMorph
            && "blockbuster.fred".equals(entityMorph.name)
            && entityMorph.userTexture == null)
        {
            return new class_2960("minecraft", "textures/entity/player/wide/steve.png");
        }

        class_897 entityRenderer = renderer.method_3953(entity);
        return entityRenderer == null ? null : entityRenderer.method_3931(entity);
    }
}
