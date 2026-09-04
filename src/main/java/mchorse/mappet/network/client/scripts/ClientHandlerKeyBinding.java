package mchorse.mappet.network.client.scripts;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketKeyBinding;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.class_304;
import net.minecraft.class_315;
import net.minecraft.class_3675;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_746;
import net.minecraft.class_310;

public class ClientHandlerKeyBinding extends ClientMessageHandler<PacketKeyBinding> {
   private static final Map<class_306, Integer> PRESSED_KEYS = new HashMap();
   private static Field boundKeyField;
   private static boolean pulseTickRegistered;

   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketKeyBinding message) {
      apply(message);
   }

   @Environment(EnvType.CLIENT)
   public static void apply(PacketKeyBinding message) {
      class_315 options = class_310.method_1551().field_1690;
      class_304 binding = find(options, message.id);
      if (binding == null) {
         return;
      }

      if (message.action == PacketKeyBinding.SET) {
         class_306 key = class_3675.method_15981(message.key);
         options.method_1641(binding, key);
      } else if (message.action == PacketKeyBinding.RESET) {
         options.method_1641(binding, binding.method_1429());
      } else if (message.action == PacketKeyBinding.REQUEST) {
         Dispatcher.sendToServer(new PacketKeyBinding(PacketKeyBinding.RESPONSE, binding.method_1431(), currentKey(binding).method_1441()));
      } else if (message.action == PacketKeyBinding.ACTIVATE) {
         activate(binding);
      }

      class_304.method_1426();
      options.method_1640();
   }

   




   @Environment(EnvType.CLIENT)
   private static void activate(class_304 binding) {
      class_306 key = currentKey(binding);
      if (key == null) {
         return;
      }

      class_304.method_1416(key, true);
      class_304.method_1420(key);
      PRESSED_KEYS.put(key, 2);
      registerPulseTick();
   }

   @Environment(EnvType.CLIENT)
   private static void registerPulseTick() {
      if (!pulseTickRegistered) {
         pulseTickRegistered = true;
         ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            Iterator<Map.Entry<class_306, Integer>> iterator = PRESSED_KEYS.entrySet().iterator();

            while(iterator.hasNext()) {
               Map.Entry<class_306, Integer> entry = (Map.Entry)iterator.next();
               int remaining = (Integer)entry.getValue() - 1;
               if (remaining <= 0) {
                  class_304.method_1416((class_306)entry.getKey(), false);
                  iterator.remove();
               } else {
                  entry.setValue(remaining);
               }
            }
         });
      }
   }

   
   @Environment(EnvType.CLIENT)
   private static class_306 currentKey(class_304 binding) {
      try {
         if (boundKeyField == null) {
            boundKeyField = class_304.class.getDeclaredField("field_1655");
            boundKeyField.setAccessible(true);
         }

         Object key = boundKeyField.get(binding);
         if (key instanceof class_306) {
            return (class_306)key;
         }
      } catch (Exception ignored) {
      }

      return binding.method_1429();
   }

   @Environment(EnvType.CLIENT)
   public static String getBindingKey(String id) {
      class_315 options = class_310.method_1551().field_1690;
      class_304 binding = find(options, id);
      class_306 key = binding == null ? null : currentKey(binding);
      return key == null ? "" : key.method_1441();
   }

   private static class_304 find(class_315 options, String id) {
      String bindingId = id == null ? "" : id;
      if (bindingId.startsWith("key_")) {
         bindingId = bindingId.substring(4);
      }

      for(class_304 binding : options.field_1839) {
         if (binding != null && binding.method_1431().equals(bindingId)) {
            return binding;
         }
      }

      return null;
   }
}
