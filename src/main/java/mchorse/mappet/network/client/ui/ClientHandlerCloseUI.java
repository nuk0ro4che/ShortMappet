package mchorse.mappet.network.client.ui;

import mchorse.mappet.client.gui.GuiUserInterface;
import mchorse.mappet.network.common.ui.PacketCloseUI;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_433;
import net.minecraft.class_437;
import net.minecraft.class_746;

public class ClientHandlerCloseUI extends ClientMessageHandler<PacketCloseUI> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketCloseUI message) {
      class_310 mc = class_310.method_1551();
      class_437 screen = mc.field_1755;
      if (screen == null) {
         return;
      }

      

      boolean mappetScreen = screen instanceof GuiUserInterface || screen.getClass().getName().startsWith("mchorse.mappet.client.gui.");
      if (mappetScreen) {
         if (message.closeMappet) {
            if (screen instanceof GuiUserInterface) {
               ((GuiUserInterface)screen).requestClose();
            } else {
               mc.method_1507((class_437)null);
            }
         }

         return;
      }

      if (!(screen instanceof class_433)) {
         mc.method_1507((class_437)null);
      }

   }
}
