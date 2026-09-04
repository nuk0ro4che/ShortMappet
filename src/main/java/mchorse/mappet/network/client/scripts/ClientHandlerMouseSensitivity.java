package mchorse.mappet.network.client.scripts;

import mchorse.mappet.mixins.GameOptionsAccessor;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketMouseSensitivity;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_315;
import net.minecraft.class_746;
import net.minecraft.class_310;

public class ClientHandlerMouseSensitivity extends ClientMessageHandler<PacketMouseSensitivity> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketMouseSensitivity message) {
      class_315 options = class_310.method_1551().field_1690;
      GameOptionsAccessor accessor = (GameOptionsAccessor)options;

      if (message.action == PacketMouseSensitivity.REQUEST) {
         Dispatcher.sendToServer(new PacketMouseSensitivity(PacketMouseSensitivity.RESPONSE, accessor.mappet$getMouseSensitivity().method_41753()));
      }
   }
}
