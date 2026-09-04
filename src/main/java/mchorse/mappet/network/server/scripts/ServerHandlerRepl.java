package mchorse.mappet.network.server.scripts;

import javax.script.ScriptException;
import mchorse.mappet.Mappet;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketRepl;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_124;
import net.minecraft.class_3222;

public class ServerHandlerRepl extends ServerMessageHandler<PacketRepl> {
   public void run(class_3222 player, PacketRepl message) {
      if (OpHelper.isPlayerOp(player)) {
         try {
            String output = Mappet.scripts.executeRepl(player, message.code);
            Dispatcher.sendTo(new PacketRepl(output), player);
         } catch (ScriptException e) {
            e.printStackTrace();
            Mappet.logger.error(e.getMessage());
            String var10002 = String.valueOf(class_124.field_1061);
            Dispatcher.sendTo(new PacketRepl(var10002 + e.getMessage()), player);
         } catch (Exception e) {
            e.printStackTrace();
            Mappet.logger.error(e.getMessage());
         }

      }
   }
}
