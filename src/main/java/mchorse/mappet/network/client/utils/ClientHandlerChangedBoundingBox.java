package mchorse.mappet.network.client.utils;

import mchorse.mappet.network.common.utils.PacketChangedBoundingBox;
import mchorse.mappet.tile.TileTrigger;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerChangedBoundingBox extends ClientMessageHandler<PacketChangedBoundingBox> {
   public void run(class_746 entityPlayerSP, PacketChangedBoundingBox message) {
      class_2586 tile = class_310.method_1551().field_1687.method_8321(message.pos);
      if (tile instanceof TileTrigger) {
         ((TileTrigger)tile).boundingBoxPos1 = message.boundingBoxPos1;
         ((TileTrigger)tile).boundingBoxPos2 = message.boundingBoxPos2;
      }

   }
}
