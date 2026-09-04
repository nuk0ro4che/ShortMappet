package mchorse.mappet.mixins;

import java.util.UUID;
import mchorse.blockbuster.api.Model;
import mchorse.blockbuster_pack.client.render.CustomMorphRenderer;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.utils.resources.ResourceLocation;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;






@Mixin(value = CustomMorphRenderer.class, remap = false)
public abstract class CustomMorphRendererSkinMixin
{
    private static final String MARKER_NAMESPACE = "mappet";
    private static final String MARKER_PREFIX = "player_skin/";
    private static final String USERNAME_PREFIX = "player_username/";

    @Redirect(
        method = "resolveTexture(Lmchorse/blockbuster_pack/morphs/CustomMorph;Lmchorse/blockbuster/api/Model;)Lnet/minecraft/class_2960;",
        at = @At(
            value = "INVOKE",
            target = "Lmchorse/mclib/utils/resources/ResourceLocation;toIdentifier()Lnet/minecraft/class_2960;",
            remap = false
        ),
        remap = false,
        require = 1
    )
    private static class_2960 mappet$resolvePlayerSkinMarker(ResourceLocation location)
    {
        class_2960 requested = location.toIdentifier();

        if (!MARKER_NAMESPACE.equals(requested.method_12836()))
        {
            return requested;
        }

        String path = requested.method_12832();

        class_310 client = class_310.method_1551();

        if (client.field_1687 == null)
        {
            return requested;
        }

        if (path.startsWith(MARKER_PREFIX))
        {
            try
            {
                UUID playerId = UUID.fromString(path.substring(MARKER_PREFIX.length()));

                for (class_742 player : client.field_1687.method_18456())
                {
                    if (playerId.equals(player.method_5667()))
                    {
                        return player.method_3117();
                    }
                }
            }
            catch (IllegalArgumentException ignored)
            {
                
            }

            return requested;
        }

        if (path.startsWith(USERNAME_PREFIX))
        {
            String username = path.substring(USERNAME_PREFIX.length());

            for (class_742 player : client.field_1687.method_18456())
            {
                if (username.equalsIgnoreCase(player.method_7334().getName()))
                {
                    


                    return player.method_3117();
                }
            }
        }

        return requested;
    }
}
