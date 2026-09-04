package mchorse.mappet.network.server.events;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.events.PacketPlayerJournal;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerPlayerJournal extends ServerMessageHandler<PacketPlayerJournal> {
   public void run(class_3222 player, PacketPlayerJournal message) {
      DataContext context = new DataContext(player);
      Mappet.settings.playerJournal.trigger(context);
      if (!context.isCanceled()) {
         Dispatcher.sendTo(message, player);
      }

   }
}
