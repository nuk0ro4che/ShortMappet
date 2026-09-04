package mchorse.mappet.network.server.ui;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.UIFile;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.ui.PacketUI;
import mchorse.mappet.network.common.ui.PacketUIPreview;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;


public class ServerHandlerUIPreview extends ServerMessageHandler<PacketUIPreview> {
   public void run(class_3222 player, PacketUIPreview message) {
      if (!OpHelper.isPlayerOp(player) || Mappet.uis == null || message.id == null || message.id.trim().isEmpty()) {
         return;
      }

      UIFile ui = message.ui;
      if (ui == null) {
         ui = (UIFile)Mappet.uis.load(message.id.trim());
      }
      if (ui == null) {
         return;
      }
      ui.setId(message.id.trim());

      ICharacter character = Character.get(player);
      if (character.getUIContext() != null) {
         character.getUIContext().close();
      }

      UIContext context = new UIContext(ui, player, ui.script, ui.function);
      character.setUIContext(context);
      context.populateDefaultData();
      context.clearChanges();

      PacketUI packet = new PacketUI(ui);
      packet.editorPreview = true;
      Dispatcher.sendTo(packet, player);
   }
}
