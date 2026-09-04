package mchorse.mappet.api.scripts.code.client;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import mchorse.mappet.network.common.scripts.PacketClipboard;

public final class ClientClipboardCache {
   private static final ConcurrentHashMap<UUID, String> CLIPBOARDS = new ConcurrentHashMap();

   private ClientClipboardCache() {
   }

   public static String get(UUID player) {
      return (String)CLIPBOARDS.getOrDefault(player, "");
   }

   public static void set(UUID player, String text) {
      CLIPBOARDS.put(player, PacketClipboard.sanitize(text));
   }

   public static void remove(UUID player) {
      CLIPBOARDS.remove(player);
   }
}
