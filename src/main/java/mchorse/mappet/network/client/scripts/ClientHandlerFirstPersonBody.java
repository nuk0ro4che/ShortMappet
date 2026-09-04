package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.FirstPersonBodyState;
import mchorse.mappet.network.common.scripts.PacketFirstPersonBody;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerFirstPersonBody extends ClientMessageHandler<PacketFirstPersonBody> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketFirstPersonBody message) {
      FirstPersonBodyState.setEnabled(message.enabled);
   }
}
