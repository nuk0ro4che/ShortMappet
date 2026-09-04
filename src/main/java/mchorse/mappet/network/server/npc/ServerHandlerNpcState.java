package mchorse.mappet.network.server.npc;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.network.common.npc.PacketNpcState;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_1297;
import net.minecraft.class_3222;

public class ServerHandlerNpcState extends ServerMessageHandler<PacketNpcState> {
   public void run(class_3222 player, PacketNpcState message) {
      class_1297 npc = player.method_37908().method_8469(message.entityId);
      if (npc instanceof EntityNpc) {
         NpcState state = new NpcState();
         state.deserializeNBT(message.state);
         ((EntityNpc)npc).setState(state, true);
      }

   }
}
