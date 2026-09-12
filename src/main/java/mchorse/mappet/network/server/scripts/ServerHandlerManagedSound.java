package mchorse.mappet.network.server.scripts;

import mchorse.mappet.api.scripts.code.sounds.ManagedSoundRegistry;
import mchorse.mappet.network.common.scripts.PacketManagedSound;
import mchorse.mappet.CommonProxy;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerManagedSound extends ServerMessageHandler<PacketManagedSound> {
   public void run(class_3222 player, PacketManagedSound message) {
      if (message.action == PacketManagedSound.TIME_CODE) {
         ManagedSoundRegistry.setTimeCode(player, message.id, message.timeCode);
      } else if (message.action == PacketManagedSound.FINISHED) {
         ManagedSoundRegistry.State state = ManagedSoundRegistry.get(player, message.id);
         if (ManagedSoundRegistry.finish(player, message.id, message.name)) {
            CommonProxy.eventHandler.onManagedSoundFinished(player, message.id, message.name, state);
         }
      }
   }
}
