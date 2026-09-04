package mchorse.mappet.network.server.content;

import mchorse.mappet.api.states.States;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketRequestStates;
import mchorse.mappet.network.common.content.PacketStates;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;

public class ServerHandlerRequestStates extends ServerMessageHandler<PacketRequestStates> {
   public void run(class_3222 player, PacketRequestStates message) {
      if (OpHelper.isPlayerOp(player)) {
         States states = ServerHandlerStates.getStates(player.method_37908().method_8503(), message.target);
         if (states != null) {
            Dispatcher.sendTo(new PacketStates(message.target, states.serializeNBT()), player);
         }

      }
   }
}
