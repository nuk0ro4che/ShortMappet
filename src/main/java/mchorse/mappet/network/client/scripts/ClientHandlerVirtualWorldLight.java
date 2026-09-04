package mchorse.mappet.network.client.scripts;

import mchorse.mappet.api.scripts.lights.VanillaWorldLightManager;
import mchorse.mappet.network.common.scripts.PacketVirtualWorldLight;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerVirtualWorldLight extends ClientMessageHandler<PacketVirtualWorldLight> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketVirtualWorldLight message) {
      if (player == null) {
         return;
      }

      if (message.action == PacketVirtualWorldLight.REMOVE) {
         VanillaWorldLightManager.removeClient(player.method_37908(), message.id);
      } else if (message.action == PacketVirtualWorldLight.SET) {
         VanillaWorldLightManager.applyClient(player.method_37908(), message.id, message.duration, message.positions, message.levels);
      }
   }
}
