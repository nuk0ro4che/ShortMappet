package mchorse.mappet.network.server.blocks;

import mchorse.mappet.network.common.blocks.PacketEditRegion;
import mchorse.mappet.tile.TileRegion;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_2586;
import net.minecraft.class_3222;

public class ServerHandlerEditRegion extends ServerMessageHandler<PacketEditRegion> {
   public void run(class_3222 player, PacketEditRegion message) {
      if (player.method_7337()) {
         class_2586 tile = WorldUtils.getBlockEntity(player.method_37908(), message.pos);
         if (tile instanceof TileRegion) {
            ((TileRegion)tile).set(message.tag);
         }

      }
   }
}
