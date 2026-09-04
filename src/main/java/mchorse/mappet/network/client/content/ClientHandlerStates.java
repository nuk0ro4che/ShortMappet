package mchorse.mappet.network.client.content;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.network.common.content.PacketStates;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerStates extends ClientMessageHandler<PacketStates> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketStates message) {
      GuiMappetDashboard.get(class_310.method_1551()).settings.fillStates(message.target, message.states);
   }
}
