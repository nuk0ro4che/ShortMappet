package mchorse.mappet.network.server.ui;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.common.ui.PacketUIData;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerUIData extends ServerMessageHandler<PacketUIData> {
   public void run(class_3222 player, PacketUIData message) {
      ICharacter character = Character.get(player);
      UIContext context = character.getUIContext();
      if (context != null) {
         context.handleNewData(message.data);
      }
   }
}
