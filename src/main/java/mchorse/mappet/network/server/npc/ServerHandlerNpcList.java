package mchorse.mappet.network.server.npc;

import java.util.Collections;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.npc.PacketNpcList;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerNpcList extends ServerMessageHandler<PacketNpcList> {
   public void run(class_3222 player, PacketNpcList message) {
      if (!message.npcs.isEmpty()) {
         Npc npc = (Npc)Mappet.npcs.load((String)message.npcs.get(0));
         if (npc != null) {
            Dispatcher.sendTo((new PacketNpcList(Collections.emptyList(), npc.states.keySet())).states(), player);
         }

      }
   }
}
