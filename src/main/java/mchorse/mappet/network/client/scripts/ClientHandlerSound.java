package mchorse.mappet.network.client.scripts;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import mchorse.mappet.network.common.scripts.PacketSound;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3419;
import net.minecraft.class_5819;
import net.minecraft.class_746;
import net.minecraft.class_1113.class_1114;

public class ClientHandlerSound extends ClientMessageHandler<PacketSound> {
   private static final Map<String, class_1109> LOOP_SOUNDS = new HashMap<>();

   private static String key(String sound, String category) {
      return sound + "\\u0000" + category;
   }

   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketSound message) {
      handle(message);
   }

   @Environment(EnvType.CLIENT)
   public static void handle(PacketSound message) {
      class_310 client = class_310.method_1551();

      if (message.stopLoop) {
         if (message.sound == null || message.sound.isEmpty()) {
            for (class_1109 sound : LOOP_SOUNDS.values()) {
               client.method_1483().method_4870(sound);
            }
            LOOP_SOUNDS.clear();
         } else if (message.soundCategory == null || message.soundCategory.isEmpty()) {
            String prefix = message.sound + "\\u0000";
            LOOP_SOUNDS.entrySet().removeIf((entry) -> {
               if (!entry.getKey().startsWith(prefix)) {
                  return false;
               }

               client.method_1483().method_4870(entry.getValue());
               return true;
            });
         } else {
            class_1109 sound = LOOP_SOUNDS.remove(key(message.sound, message.soundCategory));
            if (sound != null) {
               client.method_1483().method_4870(sound);
            }
         }

         return;
      }

      class_2960 rl = new class_2960(message.sound);

      class_3419 category;
      try {
         category = class_3419.valueOf(message.soundCategory.toUpperCase(Locale.ROOT));
      } catch (Exception var6) {
         category = class_3419.field_15250;
      }

      String soundKey = key(message.sound, message.soundCategory);
      if (message.loop) {
         class_1109 old = LOOP_SOUNDS.remove(soundKey);
         if (old != null) {
            client.method_1483().method_4870(old);
         }
      }

      class_1109 sound = new class_1109(rl, category, message.volume, message.pitch, class_5819.method_43047(), message.loop, 0, class_1114.field_5478, (double)0.0F, (double)0.0F, (double)0.0F, true);
      client.method_1483().method_4873(sound);
      if (message.loop) {
         LOOP_SOUNDS.put(soundKey, sound);
      }
   }
}
