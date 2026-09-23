package mchorse.mappet.network.client.scripts;

import mchorse.mappet.api.vision.VisionZoneManager;
import mchorse.mappet.network.common.scripts.PacketVisionZone;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerVisionZone extends ClientMessageHandler<PacketVisionZone> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketVisionZone message) {
      VisionZoneManager.applyClient(message.zones);
   }
}