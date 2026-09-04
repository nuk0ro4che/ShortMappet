package mchorse.mappet.network.server.content;

import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentData;
import mchorse.mappet.network.common.content.PacketContentRequestData;
import mchorse.mappet.utils.CurrentSession;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class ServerHandlerContentRequestData extends ServerMessageHandler<PacketContentRequestData> {
   public static boolean isOtherPlayerEdits(class_3222 except, IContentType type, String id) {
      for(class_3222 player : except.method_5682().method_3760().method_14571()) {
         if (player != except && Character.get(player).getCurrentSession().isActive(type, id)) {
            return true;
         }
      }

      return false;
   }

   public void run(class_3222 player, PacketContentRequestData message) {
      if (OpHelper.isPlayerOp(player)) {
         boolean otherEdit = isOtherPlayerEdits(player, message.type, message.name);
         class_2487 tag = (class_2487)((AbstractData)message.type.getManager().load(message.name)).serializeNBT();
         PacketContentData packet = new PacketContentData(message.type, message.name, tag);
         CurrentSession session = Character.get(player).getCurrentSession();
         if (otherEdit) {
            packet.disallow();
         }

         Dispatcher.sendTo(packet, player);
         if (!session.isEditing(message.type, message.name)) {
            ServerHandlerContentExit.syncData(player);
         }

         if (!otherEdit) {
            session.set(message.type, message.name);
         }

         session.setActive(message.type, message.name);
      }
   }
}
