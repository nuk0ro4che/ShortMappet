package mchorse.mappet.network.client.content;

import mchorse.mappet.MappetClient;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.network.common.content.PacketClientSettings;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerClientSettings extends ClientMessageHandler<PacketClientSettings> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketClientSettings message) {
      if (MappetClient.clientSettings != null) {
         MappetClient.clientSettings.fill(message.tag);
      }

      GuiMappetDashboard dashboard = GuiMappetDashboard.get(class_310.method_1551());
      if (dashboard != null && dashboard.clientSettings != null) {
         dashboard.clientSettings.fill(message.tag);
      }
   }
}