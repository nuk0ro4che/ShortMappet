package mchorse.mappet.network.server.content;

import mchorse.mappet.Mappet;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketRequestServerSettings;
import mchorse.mappet.network.common.content.PacketServerSettings;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;

public class ServerHandlerRequestServerSettings extends ServerMessageHandler<PacketRequestServerSettings> {
   public void run(class_3222 player, PacketRequestServerSettings message) {
      if (OpHelper.isPlayerOp(player)) {
         Dispatcher.sendTo(new PacketServerSettings(Mappet.settings.serializeNBT()), player);
      }
   }
}
