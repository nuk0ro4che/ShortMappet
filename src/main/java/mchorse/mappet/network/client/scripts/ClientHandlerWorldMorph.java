package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.client.morphs.WorldMorph;
import mchorse.mappet.network.common.scripts.PacketWorldMorph;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerWorldMorph extends ClientMessageHandler<PacketWorldMorph> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketWorldMorph message) {
      WorldMorph incoming = message.morph;
      if (incoming.remove) {
         RenderingHandler.removeWorldMorph(incoming.id);
      } else if (incoming.morph != null) {
         RenderingHandler.addOrReplaceWorldMorph(incoming);
      }

   }
}
