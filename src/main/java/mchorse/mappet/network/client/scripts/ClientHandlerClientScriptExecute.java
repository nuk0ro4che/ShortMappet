package mchorse.mappet.network.client.scripts;

import mchorse.mappet.Mappet;
import mchorse.mappet.MappetClient;
import mchorse.mappet.network.common.scripts.PacketClientScriptExecute;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_746;


public class ClientHandlerClientScriptExecute extends ClientMessageHandler<PacketClientScriptExecute> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketClientScriptExecute message) {
      if (MappetClient.clientScriptRuntime == null) {
         return;
      }
      try {
         MappetClient.clientScriptRuntime.receiveScripts(message.scripts);
         if (message.code != null && !message.code.isEmpty()) {
            MappetClient.clientScriptRuntime.executeInline(message.code, message.args);
         } else if (message.script != null && !message.script.isEmpty()) {
            MappetClient.clientScriptRuntime.executeClient(message.script, message.function, message.args);
         }
      } catch (Exception exception) {
         Mappet.LOGGER.error("Ошибка выполнения клиентского скрипта {}.{}", message.script, message.function, exception);
         if (player != null) {
            String error = exception.getMessage();
            player.method_7353(class_2561.method_43470("§cОшибка клиентского скрипта §f" + message.script + "§c: " + (error == null ? exception.getClass().getSimpleName() : error)), false);
         }
      }
   }
}
