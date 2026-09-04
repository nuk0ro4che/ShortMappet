package mchorse.mappet.network.client.ui;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.GuiUIEditorPreview;
import mchorse.mappet.client.gui.GuiUserInterface;
import mchorse.mappet.network.common.ui.PacketUI;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerUI extends ClientMessageHandler<PacketUI> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketUI message) {
      class_310 mc = class_310.method_1551();
      mc.method_1507(message.editorPreview ? new GuiUIEditorPreview(mc, message.ui, GuiMappetDashboard.get(mc)) : new GuiUserInterface(mc, message.ui));
   }
}
