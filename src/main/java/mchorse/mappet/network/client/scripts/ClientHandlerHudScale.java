package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.HudCustomState;
import mchorse.mappet.network.common.scripts.PacketHudScale;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerHudScale extends ClientMessageHandler<PacketHudScale> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketHudScale message) {
      HudCustomState.setScale(message.mod, message.scale);
   }
}