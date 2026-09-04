package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.ClientEntityTransitions;
import mchorse.mappet.network.common.scripts.PacketEntityTransition;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerEntityTransition extends ClientMessageHandler<PacketEntityTransition> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketEntityTransition message) {
      if (player != null) {
         ClientEntityTransitions.start(player.method_37908(), message);
      }
   }
}
