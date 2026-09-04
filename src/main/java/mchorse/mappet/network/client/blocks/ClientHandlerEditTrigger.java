package mchorse.mappet.network.client.blocks;

import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.client.gui.GuiTriggerBlockScreen;
import mchorse.mappet.network.common.blocks.PacketEditTrigger;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class ClientHandlerEditTrigger extends ClientMessageHandler<PacketEditTrigger> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketEditTrigger message) {
      Trigger left = new Trigger();
      Trigger right = new Trigger();
      left.deserializeNBT(message.left);
      right.deserializeNBT(message.right);
      class_310.method_1551().method_1507(new GuiTriggerBlockScreen(message.pos, left, right, message.collidable, message.boundingBoxPos1, message.boundingBoxPos2));
   }
}
