package mchorse.mappet.network.client.scripts;

import mchorse.mappet.network.common.scripts.PacketPlayerAction;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_429;
import net.minecraft.class_746;

public class ClientHandlerPlayerAction extends ClientMessageHandler<PacketPlayerAction> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketPlayerAction message) {
      class_310 client = class_310.method_1551();

      if (message.action == PacketPlayerAction.QUIT_MINECRAFT) {
         client.method_1574();
      } else if (message.action == PacketPlayerAction.QUIT_WORLD) {
         client.method_1490();
      } else if (message.action == PacketPlayerAction.OPEN_SETTINGS) {
         client.method_1507(new class_429(client.field_1755, client.field_1690));
      }
   }
}
