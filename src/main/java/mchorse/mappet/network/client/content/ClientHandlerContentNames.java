package mchorse.mappet.network.client.content;

import mchorse.mappet.ClientProxy;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mappet.network.common.content.PacketContentNames;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.utils.autocomplete.AutoCompleteEngine;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerContentNames extends ClientMessageHandler<PacketContentNames> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketContentNames message) {
      if (message.type == ContentType.SHADERS) {
         AutoCompleteEngine.setShaderIds(message.names);
      }
      if (message.requestId >= 0) {
         ClientProxy.process(message.names, message.requestId);
      } else {
         GuiMappetDashboard dashboard = GuiMappetDashboard.get(class_310.method_1551());
         GuiMappetDashboardPanel panel = message.type.get(dashboard);
         if (panel != null) {
            panel.fillNames(message.names);
         }

      }
   }
}
