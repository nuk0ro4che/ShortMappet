package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiScriptPanel;
import mchorse.mappet.network.common.scripts.PacketScriptSearchResults;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerScriptSearchResults extends ClientMessageHandler<PacketScriptSearchResults>
{
    @Override
    @Environment(EnvType.CLIENT)
    public void run(class_746 player, PacketScriptSearchResults message)
    {
        GuiMappetDashboard dashboard = GuiMappetDashboard.get(class_310.method_1551());
        GuiScriptPanel panel = message.clientScript ? dashboard.clientScript : dashboard.script;
        if (panel != null)
        {
            panel.showSearchResults(message.results);
        }
    }
}
