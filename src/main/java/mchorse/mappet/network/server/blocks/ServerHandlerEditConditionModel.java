package mchorse.mappet.network.server.blocks;

import mchorse.mappet.network.common.blocks.PacketEditConditionModel;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_2586;
import net.minecraft.class_3222;

public class ServerHandlerEditConditionModel extends ServerMessageHandler<PacketEditConditionModel> {
   public void run(class_3222 player, PacketEditConditionModel message) {
      if (player.method_7337()) {
         class_2586 tile = WorldUtils.getBlockEntity(player.method_37908(), message.pos);
         if (tile instanceof TileConditionModel) {
            ((TileConditionModel)tile).readFromNBT(message.tag);
         }

      }
   }
}
