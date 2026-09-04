package mchorse.mappet.network.client.npc;

import mchorse.mappet.client.gui.GuiNpcToolScreen;
import mchorse.mappet.network.common.npc.PacketNpcList;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerNpcList extends ClientMessageHandler<PacketNpcList> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketNpcList message) {
      class_310 mc = class_310.method_1551();
      if (message.isStates) {
         if (mc.field_1755 instanceof GuiNpcToolScreen) {
            GuiNpcToolScreen tool = (GuiNpcToolScreen)mc.field_1755;
            tool.states.clear();
            tool.states.add(message.states);
            tool.states.sort();
         }
      } else {
         mc.method_1507(new GuiNpcToolScreen(mc, message.npcs, message.states));
      }

   }
}
