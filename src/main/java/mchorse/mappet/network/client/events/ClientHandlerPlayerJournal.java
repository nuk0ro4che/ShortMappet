package mchorse.mappet.network.client.events;

import mchorse.mappet.client.gui.GuiJournalScreen;
import mchorse.mappet.network.common.events.PacketPlayerJournal;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerPlayerJournal extends ClientMessageHandler<PacketPlayerJournal> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketPlayerJournal message) {
      class_310 mc = class_310.method_1551();
      mc.method_1507(new GuiJournalScreen(mc));
   }
}
