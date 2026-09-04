package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.HudVisibilityState;
import mchorse.mappet.network.common.scripts.PacketHudVisibility;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerHudVisibility extends ClientMessageHandler<PacketHudVisibility> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketHudVisibility message) {
      HudVisibilityState.Element[] elements = HudVisibilityState.Element.values();
      if (message.element >= 0 && message.element < elements.length) {
         HudVisibilityState.set(elements[message.element], message.visible);
      }
   }
}
