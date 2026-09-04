package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.HudVisibilityState;
import mchorse.mappet.network.common.scripts.PacketHudPosition;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerHudPosition extends ClientMessageHandler<PacketHudPosition> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketHudPosition message) {
      HudVisibilityState.Element[] elements = HudVisibilityState.Element.values();
      if (message.element >= 0 && message.element < elements.length) {
         HudVisibilityState.setPosition(elements[message.element], message.x, message.y);
      }
   }
}
