package mchorse.mappet;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.content.PacketContentRequestNames;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientProxy extends CommonProxy {
   private static int requestId;
   private static final Map<Integer, Consumer<List<String>>> consumers = new HashMap();
   public static File sounds;

   public static void requestNames(IContentType type, Consumer<List<String>> consumer) {
      consumers.put(requestId, consumer);
      Dispatcher.sendToServer(new PacketContentRequestNames(type, requestId++));
   }

   public static void process(List<String> names, int id) {
      Consumer<List<String>> consumer = (Consumer)consumers.remove(id);
      if (consumer != null) {
         consumer.accept(names);
      }

   }
}
