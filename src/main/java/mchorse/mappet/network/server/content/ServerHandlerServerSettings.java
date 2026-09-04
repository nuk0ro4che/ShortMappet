package mchorse.mappet.network.server.content;

import mchorse.mappet.Mappet;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketServerSettings;
import mchorse.mappet.network.common.events.PacketEventHotkeys;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;

public class ServerHandlerServerSettings extends ServerMessageHandler<PacketServerSettings> {
   public void run(class_3222 player, PacketServerSettings message) {
      if (OpHelper.isPlayerOp(player)) {
         Mappet.settings.deserializeNBT(message.tag);
         Mappet.settings.save();

         for(class_3222 p : player.method_5682().method_3760().method_14571()) {
            Dispatcher.sendTo(new PacketEventHotkeys(Mappet.settings), p);
         }

      }
   }
}
