package mchorse.mappet.network.server.scripts;

import mchorse.mappet.Mappet;
import mchorse.mappet.network.common.scripts.PacketClick;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_3222;

public class ServerHandlerClick extends ServerMessageHandler<PacketClick> {
   public void run(class_3222 player, PacketClick message) {
      if (message.hand == class_1268.field_5808 && !Mappet.settings.playerLeftClick.isEmpty()) {
         Mappet.settings.playerLeftClick.trigger((class_1309)player);
      } else if (message.hand == class_1268.field_5810 && !Mappet.settings.playerRightClick.isEmpty()) {
         Mappet.settings.playerRightClick.trigger((class_1309)player);
      }

   }
}
