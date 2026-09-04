package mchorse.mappet.network.server.blocks;

import mchorse.mappet.network.common.blocks.PacketEditEmitter;
import mchorse.mappet.tile.TileEmitter;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_2586;
import net.minecraft.class_3222;

public class ServerHandlerEditEmitter extends ServerMessageHandler<PacketEditEmitter> {
   public void run(class_3222 player, PacketEditEmitter message) {
      if (player.method_7337()) {
         class_2586 tile = WorldUtils.getBlockEntity(player.method_37908(), message.pos);
         if (tile instanceof TileEmitter) {
            ((TileEmitter)tile).setExpression(message);
         }

      }
   }
}
