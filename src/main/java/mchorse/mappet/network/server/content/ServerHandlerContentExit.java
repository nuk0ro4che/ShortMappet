package mchorse.mappet.network.server.content;

import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentData;
import mchorse.mappet.network.common.content.PacketContentExit;
import mchorse.mappet.utils.CurrentSession;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class ServerHandlerContentExit extends ServerMessageHandler<PacketContentExit> {
   public static void syncData(class_3222 except) {
      CurrentSession session = Character.get(except).getCurrentSession();
      class_2487 data = null;
      int i = 0;
      if (session.type != null) {
         for(class_3222 player : except.method_5682().method_3760().method_14571()) {
            if (player != except) {
               CurrentSession otherSession = Character.get(player).getCurrentSession();
               if (otherSession.isActive(session.type, session.id)) {
                  if (data == null) {
                     data = (class_2487)((AbstractData)session.type.getManager().load(session.id)).serializeNBT();
                  }

                  PacketContentData packet = new PacketContentData(session.type, session.id, data);
                  if (i > 0) {
                     packet.disallow();
                  }

                  Dispatcher.sendTo(packet, player);
                  if (i == 0) {
                     otherSession.set(session.type, session.id);
                  }

                  ++i;
               }
            }
         }

      }
   }

   public void run(class_3222 player, PacketContentExit message) {
      syncData(player);
      Character.get(player).getCurrentSession().reset();
   }
}
