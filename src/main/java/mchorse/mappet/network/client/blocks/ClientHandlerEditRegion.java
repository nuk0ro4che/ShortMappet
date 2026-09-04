package mchorse.mappet.network.client.blocks;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.network.common.blocks.PacketEditRegion;
import mchorse.mappet.tile.TileRegion;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerEditRegion extends ClientMessageHandler<PacketEditRegion> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketEditRegion message) {
      class_2586 tile = player.method_37908().method_8321(message.pos);
      if (tile instanceof TileRegion region) {
         region.set(message.tag);
         if (message.open) {
            GuiMappetDashboard dashboard = GuiMappetDashboard.get(class_310.method_1551());
            dashboard.panels.setPanel(dashboard.region);
            dashboard.region.fill(region, true);
            class_310.method_1551().method_1507(dashboard);
         }
      }

   }
}
