package mchorse.mappet.network.client.scripts;

import java.net.URI;
import java.util.Locale;
import mchorse.mappet.network.common.scripts.PacketOpenWeb;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_156;
import net.minecraft.class_746;

public class ClientHandlerOpenWeb extends ClientMessageHandler<PacketOpenWeb> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketOpenWeb message) {
      try {
         URI uri = new URI(message.url);
         String scheme = uri.getScheme();
         if (scheme == null || uri.getHost() == null) {
            return;
         }

         scheme = scheme.toLowerCase(Locale.ROOT);
         if (!"http".equals(scheme) && !"https".equals(scheme)) {
            return;
         }

         class_156.method_668().method_673(uri);
      } catch (Exception var5) {
      }
   }
}
