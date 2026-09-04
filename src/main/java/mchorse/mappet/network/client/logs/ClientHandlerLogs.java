package mchorse.mappet.network.client.logs;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.network.common.logs.PacketLogs;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerLogs extends ClientMessageHandler<PacketLogs> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketLogs message) {
      GuiMappetDashboard.get(class_310.method_1551()).logs.update(message.text);
   }
}
