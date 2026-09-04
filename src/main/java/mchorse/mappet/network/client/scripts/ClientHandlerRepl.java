package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.network.common.scripts.PacketRepl;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerRepl extends ClientMessageHandler<PacketRepl> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketRepl message) {
      GuiMappetDashboard.get(class_310.method_1551()).script.repl.log(message.code);
   }
}
