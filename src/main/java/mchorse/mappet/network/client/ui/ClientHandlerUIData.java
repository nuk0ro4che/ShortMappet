package mchorse.mappet.network.client.ui;

import mchorse.mappet.client.gui.GuiUserInterface;
import mchorse.mappet.network.common.ui.PacketUIData;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerUIData extends ClientMessageHandler<PacketUIData> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketUIData message) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1755 instanceof GuiUserInterface) {
         GuiUserInterface screen = (GuiUserInterface)mc.field_1755;
         screen.handleUIChanges(message.data);
      }

   }
}
