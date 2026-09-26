package mchorse.mappet.network.client.scripts;

import mchorse.mappet.network.common.scripts.PacketBlockBreakSpeed;
import mchorse.mappet.utils.MappetBlockBreakSpeed;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerBlockBreakSpeed extends ClientMessageHandler<PacketBlockBreakSpeed> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketBlockBreakSpeed message) {
      if (message.reset) {
         MappetBlockBreakSpeed.remove(player.method_5667());
      } else {
         MappetBlockBreakSpeed.set(player.method_5667(), message.multiplier);
      }
   }
}