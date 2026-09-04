package mchorse.mappet.network.server.content;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.states.States;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.network.common.content.PacketStates;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class ServerHandlerStates extends ServerMessageHandler<PacketStates> {
   public static States getStates(MinecraftServer server, String target) {
      if (target.equals("~")) {
         return Mappet.states;
      } else {
         class_3222 player = server.method_3760().method_14566(target);
         return target != null ? Character.get(player).getStates() : null;
      }
   }

   public void run(class_3222 player, PacketStates message) {
      if (OpHelper.isPlayerOp(player)) {
         States states = getStates(player.method_37908().method_8503(), message.target);
         if (states != null) {
            states.deserializeNBT(message.states);
         }

      }
   }
}
