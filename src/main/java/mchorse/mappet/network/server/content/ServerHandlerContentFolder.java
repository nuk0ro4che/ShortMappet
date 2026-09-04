package mchorse.mappet.network.server.content;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.utils.manager.IManager;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentFolder;
import mchorse.mappet.network.common.content.PacketContentNames;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerContentFolder extends ServerMessageHandler<PacketContentFolder> {
   public void run(class_3222 player, PacketContentFolder message) {
      IManager manager = message.type.getManager();
      Path folder = manager.getFolder().toPath();
      if (message.rename != null && !message.path.isEmpty()) {
         int lastIndex = message.path.lastIndexOf(47);
         String newPath = lastIndex == -1 ? "" + message.rename : message.path.substring(0, lastIndex + 1) + message.rename;
         folder.resolve(message.path).toFile().renameTo(folder.resolve(newPath).toFile());
      } else if (message.delete && !message.path.isEmpty()) {
         File deleteFolder = folder.resolve(message.path).toFile();

         for(File file : deleteFolder.listFiles()) {
            file.delete();
         }

         deleteFolder.delete();
      } else {
         folder.resolve(message.path + message.name).toFile().mkdirs();
      }

      List<String> names = new ArrayList(message.type.getManager().getKeys());

      for(class_3222 otherPlayer : player.method_5682().method_3760().method_14571()) {
         Dispatcher.sendTo(new PacketContentNames(message.type, names), otherPlayer);
      }

   }
}
