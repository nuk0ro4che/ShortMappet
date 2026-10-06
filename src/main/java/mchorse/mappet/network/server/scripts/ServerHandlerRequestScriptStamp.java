package mchorse.mappet.network.server.scripts;

import java.io.File;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.ScriptManager;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketRequestScriptStamp;
import mchorse.mappet.network.common.scripts.PacketScriptStamp;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_3222;


/** Replies with the current timestamp of a script file so the editor can notice edits made outside of the game */
public class ServerHandlerRequestScriptStamp extends ServerMessageHandler<PacketRequestScriptStamp> {
   public void run(class_3222 player, PacketRequestScriptStamp message) {
      if (!OpHelper.isPlayerOp(player) || message.script == null || message.script.isEmpty()) {
         return;
      }

      ScriptManager manager = message.clientScript ? Mappet.clientScripts : Mappet.scripts;

      if (manager == null) {
         return;
      }

      File file = manager.getScriptFile(message.script);
      boolean exists = file != null && file.exists();
      File data = manager.getFile(message.script);

      Dispatcher.sendTo(new PacketScriptStamp(
              message.script,
              exists ? file.lastModified() : 0L,
              exists ? file.length() : -1L,
              data != null && data.exists() ? data.lastModified() : 0L), player);
   }
}
