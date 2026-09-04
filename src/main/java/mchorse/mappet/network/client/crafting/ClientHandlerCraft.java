package mchorse.mappet.network.client.crafting;

import mchorse.mappet.client.gui.crafting.ICraftingScreen;
import mchorse.mappet.network.common.crafting.PacketCraft;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_746;

public class ClientHandlerCraft extends ClientMessageHandler<PacketCraft> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketCraft message) {
      class_437 screen = class_310.method_1551().field_1755;
      if (screen instanceof ICraftingScreen) {
         ((ICraftingScreen)screen).refresh();
      }

   }
}
