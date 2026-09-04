package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.CameraShakeHandler;
import mchorse.mappet.network.common.scripts.PacketCameraShake;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerCameraShake extends ClientMessageHandler<PacketCameraShake> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketCameraShake message) {
      CameraShakeHandler.set(message.active, message.ticks, message.pitch, message.yaw, message.roll, message.frequency);
   }
}
