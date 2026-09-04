package mchorse.mappet.network.server.scripts;

import mchorse.mappet.api.scripts.code.client.ClientClipboardCache;
import mchorse.mappet.network.common.scripts.PacketClipboard;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerClipboard extends ServerMessageHandler<PacketClipboard> {
   public void run(class_3222 player, PacketClipboard message) {
      if (message.action == PacketClipboard.RESPONSE) {
         ClientClipboardCache.set(player.method_5667(), message.text);
      }
   }
}
