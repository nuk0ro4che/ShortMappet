package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.sounds.ClientManagedSoundManager;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerManagedSound extends ClientMessageHandler<PacketManagedSound> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketManagedSound message) {
      ClientManagedSoundManager.handle(message);
   }
}
