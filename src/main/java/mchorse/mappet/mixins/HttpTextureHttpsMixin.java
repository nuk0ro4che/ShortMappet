package mchorse.mappet.mixins;

import net.minecraft.class_1046;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Some ISPs and proxy/VPN setups break plain HTTP (port 80) while HTTPS keeps
 * working. Minecraft downloads skins, capes and elytra textures from
 * {@code http://textures.minecraft.net/...}, so on such networks no skin can be
 * loaded by any launcher (vanilla, CustomSkinLoader, Essential, ...).
 *
 * <p>The very same files are served over HTTPS too, so we simply upgrade the
 * URL scheme right before {@code HttpTexture} opens the connection. This keeps
 * working even when port 80 is completely dropped, while doing nothing for
 * regular servers that only expose HTTP.</p>
 */
@Mixin(class_1046.class)
public class HttpTextureHttpsMixin
{
    private static final String[] SKIN_HOSTS = {
        "textures.minecraft.net",
        "skinsystem.ely.by",
        "littleskin.cn"
    };

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, index = 2)
    private static String mappet$upgradeSkinUrlToHttps(String url)
    {
        if (url == null || !url.startsWith("http://"))
        {
            return url;
        }

        for (String host : SKIN_HOSTS)
        {
            if (url.indexOf(host) >= 0)
            {
                return "https://" + url.substring("http://".length());
            }
        }

        return url;
    }
}
