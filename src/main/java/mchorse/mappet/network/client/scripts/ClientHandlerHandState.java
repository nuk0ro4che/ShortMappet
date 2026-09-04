package mchorse.mappet.network.client.scripts;

import mchorse.mappet.hand.Hands;
import mchorse.mappet.network.common.scripts.PacketHandState;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerHandState extends ClientMessageHandler<PacketHandState> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketHandState message) {
      Hands.set(message.player, message.state);
   }
}
