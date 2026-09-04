package mchorse.mappet.mixins;

import java.util.Locale;
import mchorse.blockbuster_pack.morphs.CustomMorph;
import mchorse.mclib.utils.resources.RLUtils;
import net.minecraft.class_2487;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;





@Mixin(value = CustomMorph.class, remap = false)
public abstract class CustomMorphUsernameMixin
{
    private static final String PLAYER_USERNAME_PREFIX = "mappet:player_username/";

    @Inject(method = "fromNBT(Lnet/minecraft/class_2487;)V", at = @At("TAIL"), remap = false, require = 1)
    private void mappet$applyUsernameSkin(class_2487 tag, CallbackInfo ci)
    {
        CustomMorph morph = (CustomMorph)(Object)this;

        if (tag.method_10545("Skin") || !tag.method_10545("Username") || !mappet$isPlayerActor(morph.name))
        {
            return;
        }

        String username = tag.method_10558("Username").trim();

        

        if (!username.matches("[A-Za-z0-9_]{1,16}"))
        {
            return;
        }

        morph.skin = RLUtils.create(PLAYER_USERNAME_PREFIX + username.toLowerCase(Locale.ROOT));
    }

    private static boolean mappet$isPlayerActor(String name)
    {
        return "blockbuster.alex".equals(name) || "blockbuster.fred".equals(name);
    }
}
