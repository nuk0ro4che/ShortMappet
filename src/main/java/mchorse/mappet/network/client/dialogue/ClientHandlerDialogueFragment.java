package mchorse.mappet.network.client.dialogue;

import mchorse.mappet.client.gui.GuiInteractionScreen;
import mchorse.mappet.network.common.dialogue.PacketDialogueFragment;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_746;

public class ClientHandlerDialogueFragment extends ClientMessageHandler<PacketDialogueFragment> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketDialogueFragment message) {
      class_437 screen = class_310.method_1551().field_1755;
      if (screen instanceof GuiInteractionScreen dialogue) {
         dialogue.pickReply(message);
      } else if (!message.reaction.text.isEmpty() || !message.isEmpty()) {
         class_310.method_1551().method_1507(new GuiInteractionScreen(message));
      }

   }
}
