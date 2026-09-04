package mchorse.mappet.network.client.blocks;

import mchorse.mappet.client.gui.GuiEmitterBlockScreen;
import mchorse.mappet.network.common.blocks.PacketEditEmitter;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerEditEmitter extends ClientMessageHandler<PacketEditEmitter> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketEditEmitter message) {
      class_310.method_1551().method_1507(new GuiEmitterBlockScreen(message));
   }
}
