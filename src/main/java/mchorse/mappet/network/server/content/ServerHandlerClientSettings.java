package mchorse.mappet.network.server.content;

import mchorse.mappet.Mappet;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketClientSettings;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;

public class ServerHandlerClientSettings extends ServerMessageHandler<PacketClientSettings> {
   public void run(class_3222 player, PacketClientSettings message) {
      if (OpHelper.isPlayerOp(player) && Mappet.clientSettings != null) {
         Mappet.clientSettings.fill(message.tag);
         Mappet.clientSettings.save();

         for (class_3222 p : player.method_5682().method_3760().method_14571()) {
            Dispatcher.sendTo(new PacketClientSettings(Mappet.clientSettings.serializeNBT()), p);
         }
      }
   }
}
