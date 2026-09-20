package mchorse.mappet.network.server.content;

import mchorse.mappet.Mappet;
import mchorse.mappet.network.common.content.PacketGlobalMigrate;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_1074;
import net.minecraft.class_2561;
import net.minecraft.class_3222;

public class ServerHandlerGlobalMigrate extends ServerMessageHandler<PacketGlobalMigrate> {
   public void run(class_3222 player, PacketGlobalMigrate message) {
      if (OpHelper.isPlayerOp(player)) {
         String result = Mappet.migrateConfigToGlobal(player.method_5682());
         player.method_7353(class_2561.method_43470(class_1074.method_4662(result)), false);
      }
   }
}