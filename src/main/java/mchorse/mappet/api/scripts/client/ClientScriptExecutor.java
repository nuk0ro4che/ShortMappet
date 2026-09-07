package mchorse.mappet.api.scripts.client;

import java.util.LinkedHashMap;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketClientScriptExecute;
import net.minecraft.class_2487;
import net.minecraft.class_3222;





public final class ClientScriptExecutor {
   private ClientScriptExecutor() {
   }

   public static boolean execute(class_3222 player, String scriptId) {
      return execute(player, scriptId, "main");
   }

   public static boolean execute(class_3222 player, String scriptId, String function) {
      return execute(player, scriptId, function, (Object[])null);
   }

   public static boolean execute(class_3222 player, String scriptId, String function, Object... args) {
      if (player == null || Mappet.clientScripts == null || scriptId == null) {
         return false;
      }

      String id = scriptId.trim();
      if (id.isEmpty()) {
         return false;
      }

      Script script = Mappet.clientScripts.load(id);
      if (script == null) {
         return false;
      }

      String entry = function == null || function.trim().isEmpty() ? "main" : function.trim();
      Map<String, class_2487> payload = buildClientPayload(player, id);

      Dispatcher.sendTo(new PacketClientScriptExecute(id, entry, payload, args), player);
      return true;
   }

   public static boolean executeInline(class_3222 player, String source, Object... args) {
      if (player == null || source == null || source.trim().isEmpty() || Mappet.clientScripts == null) {
         return false;
      }

      Map<String, class_2487> payload = buildClientPayload(player, null);
      PacketClientScriptExecute message = new PacketClientScriptExecute(source, args);
      message.scripts.putAll(payload);
      Dispatcher.sendTo(message, player);
      return true;
   }

   private static Map<String, class_2487> buildClientPayload(class_3222 player, String targetId) {
      Map<String, class_2487> payload = new LinkedHashMap<>();

      for(String currentId : Mappet.clientScripts.getKeys()) {
         if (currentId.endsWith("/")) {
            continue;
         }

         Script current = Mappet.clientScripts.load(currentId);
         if (current != null && (current.client || currentId.equals(targetId))) {
            payload.put(currentId, current.serializeNBT());
         }
      }

      return payload;
   }
}
