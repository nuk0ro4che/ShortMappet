package mchorse.mappet.network.server.blocks;

import mchorse.mappet.network.common.blocks.PacketEditTrigger;
import mchorse.mappet.tile.TileTrigger;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_2586;
import net.minecraft.class_3222;

public class ServerHandlerEditTrigger extends ServerMessageHandler<PacketEditTrigger> {
   public void run(class_3222 player, PacketEditTrigger message) {
      if (player.method_7337()) {
         class_2586 tile = WorldUtils.getBlockEntity(player.method_37908(), message.pos);
         if (tile instanceof TileTrigger) {
            ((TileTrigger)tile).set(message.left, message.right, message.collidable, message.boundingBoxPos1, message.boundingBoxPos2);
         }

      }
   }
}
