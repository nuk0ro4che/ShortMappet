package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiScriptPanel;
import mchorse.mappet.network.common.scripts.PacketScriptDiagnosticCode;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_746;

public class ClientHandlerScriptDiagnosticCode extends ClientMessageHandler<PacketScriptDiagnosticCode> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketScriptDiagnosticCode message) {
      class_437 screen = class_310.method_1551().field_1755;
      if (screen instanceof GuiMappetDashboard dashboard) {
         GuiScriptPanel panel = message.clientScript ? dashboard.clientScript : dashboard.script;
         if (panel != null) {
            panel.receiveScriptDiagnosticCode(message.script, message.code, message.libraryFunctions, message.library);
         }
      }
   }
}
