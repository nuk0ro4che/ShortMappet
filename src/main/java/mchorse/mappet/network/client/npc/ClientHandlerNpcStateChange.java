package mchorse.mappet.network.client.npc;

import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.network.common.npc.PacketNpcStateChange;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_746;

public class ClientHandlerNpcStateChange extends ClientMessageHandler<PacketNpcStateChange> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketNpcStateChange message) {
      class_1297 entity = player.method_37908().method_8469(message.id);
      if (entity instanceof EntityNpc) {
         ((EntityNpc)entity).setState(message.state, true);
      }

   }
}
