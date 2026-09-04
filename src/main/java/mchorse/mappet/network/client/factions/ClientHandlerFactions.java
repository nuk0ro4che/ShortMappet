package mchorse.mappet.network.client.factions;

import mchorse.mappet.client.gui.GuiJournalScreen;
import mchorse.mappet.network.common.factions.PacketFactions;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerFactions extends ClientMessageHandler<PacketFactions> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketFactions message) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1755 instanceof GuiJournalScreen) {
         ((GuiJournalScreen)mc.field_1755).fillFactions(message.factions, message.states);
      }

   }
}
