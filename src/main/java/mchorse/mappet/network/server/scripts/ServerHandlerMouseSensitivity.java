package mchorse.mappet.network.server.scripts;

import mchorse.mappet.api.scripts.code.client.ClientMouseSensitivityCache;
import mchorse.mappet.network.common.scripts.PacketMouseSensitivity;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerMouseSensitivity extends ServerMessageHandler<PacketMouseSensitivity> {
   public void run(class_3222 player, PacketMouseSensitivity message) {
      if (message.action == PacketMouseSensitivity.RESPONSE) {
         ClientMouseSensitivityCache.set(player.method_5667(), clamp(message.value));
      }
   }

   private double clamp(double value) {
      return Math.max(0.0D, Math.min(1.0D, value));
   }
}
