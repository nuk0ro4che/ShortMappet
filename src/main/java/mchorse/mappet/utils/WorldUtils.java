package mchorse.mappet.utils;

import java.util.Locale;
import net.minecraft.class_1109;
import net.minecraft.class_1144;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2767;
import net.minecraft.class_2770;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3222;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_6880;

public class WorldUtils {
   public static class_2586 getBlockEntity(class_1937 world, class_2338 pos) {
      return world.method_22340(pos) ? world.method_8321(pos) : null;
   }

   public static void playSound(class_3222 player, String event, String category) {
      playSound(player, event, category, player.method_23317(), player.method_23318(), player.method_23321(), 1.0F, 1.0F);
   }

   public static void playSound(class_3222 player, String event) {
      playSound(player, event, "master");
   }

   public static void playSound(class_3222 player, String event, String category, double x, double y, double z, float volume, float pitch) {
      class_2960 id = class_2960.method_12829(event);
      if (id != null) {
         class_3419 soundCategory;
         try {
            soundCategory = class_3419.valueOf(category.toUpperCase(Locale.ROOT));
         } catch (Exception var14) {
            soundCategory = class_3419.field_15250;
         }

         player.field_13987.method_14364(new class_2767(class_6880.method_40223(class_3414.method_47908(id)), soundCategory, x + 0.5, y, z + 0.5, volume, pitch, player.method_6051().method_43055()));
      }
   }

   public static void playSound(class_3222 player, String event, double x, double y, double z, float volume, float pitch) {
      playSound(player, event, "master", x, y, z, volume, pitch);
   }

   public static void stopSound(class_3222 player, String event) {
      player.field_13987.method_14364(new class_2770(class_2960.method_12829(event), (class_3419)null));
   }

   


   public static void playSound(class_1937 world, double x, double y, double z, String event, float volume, float pitch) {
      class_2960 id = class_2960.method_12829(event);
      if (id != null) {
         class_310 client = class_310.method_1551();
         if (client != null) {
            class_1109 sound = class_1109.method_4757(class_3414.method_47908(id), volume, pitch);
            client.method_1483().method_4873(sound);
         }
      }
   }
}
