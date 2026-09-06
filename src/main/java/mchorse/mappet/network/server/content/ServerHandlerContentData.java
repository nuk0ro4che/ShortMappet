package mchorse.mappet.network.server.content;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.api.utils.manager.IManager;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentData;
import mchorse.mappet.network.common.content.PacketContentNames;
import mchorse.mappet.network.common.scripts.PacketClientScriptExecute;
import mchorse.mappet.utils.CurrentSession;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class ServerHandlerContentData extends ServerMessageHandler<PacketContentData> {
   public void run(class_3222 player, PacketContentData message) {
      boolean isEditing = !Character.get(player).getCurrentSession().isEditing(message.type, message.name);
      boolean exists = message.type.getManager().exists(message.name);
      boolean targetExists = message.rename != null && message.type.getManager().exists(message.rename);
      boolean creating = !exists && message.rename == null && message.data != null;
      boolean editing = exists && (!isEditing || message.rename != null);
      if (OpHelper.isPlayerOp(player) && !targetExists && (creating || editing)) {
         IManager manager = message.type.getManager();
         if (message.rename != null) {
            if (!manager.rename(message.name, message.rename)) {
               return;
            }
            if (message.data != null) {
               manager.save(message.rename, message.data);
            }
         } else if (message.data == null) {
            manager.delete(message.name);
         } else {
            manager.save(message.name, message.data);
         }

         if (!exists && manager.exists(message.name)) {
            CurrentSession session = Character.get(player).getCurrentSession();
            session.set(message.type, message.name);
            session.setActive(message.type, message.name);
         }

         List<String> names = new ArrayList(message.type.getManager().getKeys());

         for(class_3222 otherPlayer : player.method_5682().method_3760().method_14571()) {
            if (otherPlayer != player) {
               CurrentSession session = Character.get(otherPlayer).getCurrentSession();
               Dispatcher.sendTo(new PacketContentNames(message.type, names), otherPlayer);
               if (session.isActive(message.type, message.name)) {
                  Dispatcher.sendTo(message.disallow(), otherPlayer);
               }
            }
         }

         if (message.type == ContentType.SCRIPTS) {
            this.pushClientScriptsToAllPlayers();
         }

      }
   }

   private void pushClientScriptsToAllPlayers() {
      if (Mappet.clientScripts == null) {
         return;
      }

      Map<String, class_2487> payload = new LinkedHashMap<>();

      for (String id : Mappet.clientScripts.getKeys()) {
         if (id.endsWith("/")) {
            continue;
         }
         Script script = Mappet.clientScripts.load(id);
         if (script != null && script.client) {
            payload.put(id, script.serializeNBT());
         }
      }

      if (payload.isEmpty()) {
         return;
      }

      PacketClientScriptExecute packet = new PacketClientScriptExecute("", "", payload);

      for (class_3222 onlinePlayer : Mappet.server.method_3760().method_14571()) {
         Dispatcher.sendTo(packet, onlinePlayer);
      }
   }
}
