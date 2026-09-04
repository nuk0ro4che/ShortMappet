package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.ClientMovementLockState;
import mchorse.mappet.network.common.scripts.PacketMovementLock;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerMovementLock extends ClientMessageHandler<PacketMovementLock> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketMovementLock message) {
      if (message.action == PacketMovementLock.JUMP) {
         ClientMovementLockState.setJumpDisabled(message.disabled);
      } else if (message.action == PacketMovementLock.SPRINT) {
         ClientMovementLockState.setSprintDisabled(message.disabled);
      }
   }
}
