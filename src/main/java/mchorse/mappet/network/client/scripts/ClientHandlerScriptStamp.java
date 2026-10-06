package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiScriptPanel;
import mchorse.mappet.network.common.scripts.PacketScriptStamp;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_746;

@Environment(EnvType.CLIENT)
public class ClientHandlerScriptStamp extends ClientMessageHandler<PacketScriptStamp> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketScriptStamp message) {
      class_437 screen = class_310.method_1551().field_1755;
      if (screen instanceof GuiMappetDashboard dashboard) {
         GuiScriptPanel panel = dashboard.script;
         if (panel != null) {
            panel.receiveScriptStamp(message.script, message.modified, message.length, message.dataModified);
         }
      }
   }
}
