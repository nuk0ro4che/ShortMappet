package mchorse.mappet.network.server.content;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentNames;
import mchorse.mappet.network.common.content.PacketContentRequestNames;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;

public class ServerHandlerContentRequestNames extends ServerMessageHandler<PacketContentRequestNames> {
   public void run(class_3222 player, PacketContentRequestNames message) {
      if (OpHelper.isPlayerOp(player)) {
         List<String> names = new ArrayList(message.type.getManager().getKeys());
         Dispatcher.sendTo(new PacketContentNames(message.type, names, message.requestId), player);
      }
   }
}
