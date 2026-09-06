package mchorse.mappet.network.server.scripts;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketClientScriptFlags;
import mchorse.mappet.network.common.scripts.PacketRequestClientScriptFlags;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerRequestClientScriptFlags extends ServerMessageHandler<PacketRequestClientScriptFlags> {
   public void run(class_3222 player, PacketRequestClientScriptFlags message) {
      List<String> ids = new ArrayList<>();

      if (Mappet.scripts != null) {
         for (String id : Mappet.scripts.getKeys()) {
            if (id.endsWith("/")) {
               continue;
            }

            Script script = Mappet.scripts.load(id);
            if (script != null && script.client) {
               ids.add(id);
            }
         }
      }

      Dispatcher.sendTo(new PacketClientScriptFlags(ids), player);
   }
}