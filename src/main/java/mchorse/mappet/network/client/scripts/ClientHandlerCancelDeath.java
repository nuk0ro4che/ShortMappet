package mchorse.mappet.network.client.scripts;

import mchorse.mappet.network.common.scripts.PacketCancelDeath;
import mchorse.mclib.network.ClientMessageHandler;
import net.minecraft.class_310;
import net.minecraft.class_746;




public class ClientHandlerCancelDeath extends ClientMessageHandler<PacketCancelDeath> {
   @Override
   public void run(class_746 player, PacketCancelDeath message) {
      if (message.cancel) {
         class_310 mc = class_310.method_1551();
         if (mc.field_1755 != null) {
            mc.method_1507(null);
         }
      }
   }
}