package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.ClientSprintHungerState;
import mchorse.mappet.network.common.scripts.PacketSprintHunger;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerSprintHunger extends ClientMessageHandler<PacketSprintHunger> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketSprintHunger message) {
      ClientSprintHungerState.setDisabled(message.disabled);
   }
}
