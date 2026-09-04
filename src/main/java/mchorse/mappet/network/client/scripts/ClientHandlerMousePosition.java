package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.ClientMousePositionController;
import mchorse.mappet.client.ExternalPointerBridge;
import mchorse.mappet.network.common.scripts.PacketMousePosition;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerMousePosition extends ClientMessageHandler<PacketMousePosition> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketMousePosition message) {
      apply(message);
   }

   @Environment(EnvType.CLIENT)
   public static void apply(PacketMousePosition message) {
      boolean external = message.relative
         ? ExternalPointerBridge.sendMoveBy(message.x, message.y, message.duration, message.interpolation)
         : message.duration <= 0
            ? ExternalPointerBridge.sendSet(message.x, message.y)
            : ExternalPointerBridge.sendMove(message.x, message.y, message.duration, message.interpolation);
      if (!external) {
         if (message.relative) {
            ClientMousePositionController.moveBy(message.x, message.y, message.duration, message.interpolation);
         } else if (message.duration <= 0) {
            ClientMousePositionController.set(message.x, message.y);
         } else {
            ClientMousePositionController.moveTo(message.x, message.y, message.duration, message.interpolation);
         }
      }
   }
}
