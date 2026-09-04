package mchorse.mappet.network.client.npc;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.client.gui.GuiNpcStateScreen;
import mchorse.mappet.network.common.npc.PacketNpcState;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerNpcState extends ClientMessageHandler<PacketNpcState> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketNpcState message) {
      class_310 mc = class_310.method_1551();
      NpcState state = new NpcState();
      state.deserializeNBT(message.state);
      mc.method_1507(new GuiNpcStateScreen(mc, message.entityId, state));
   }
}
