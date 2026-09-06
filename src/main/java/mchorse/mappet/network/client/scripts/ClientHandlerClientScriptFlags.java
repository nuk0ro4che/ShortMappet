package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiScriptPanel;
import mchorse.mappet.network.common.scripts.PacketClientScriptFlags;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerClientScriptFlags extends ClientMessageHandler<PacketClientScriptFlags> {
   @Override
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketClientScriptFlags message) {
      GuiMappetDashboard dashboard = GuiMappetDashboard.get(class_310.method_1551());
      GuiScriptPanel panel = dashboard == null ? null : dashboard.script;
      if (panel != null) {
         panel.setClientScriptIds(message.ids);
      }
   }
}