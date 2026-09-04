package mchorse.mappet.network.client.crafting;

import mchorse.mappet.client.gui.GuiCraftingTableScreen;
import mchorse.mappet.network.common.crafting.PacketCraftingTable;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerCraftingTable extends ClientMessageHandler<PacketCraftingTable> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketCraftingTable message) {
      class_310.method_1551().method_1507(new GuiCraftingTableScreen(message.table));
   }
}
