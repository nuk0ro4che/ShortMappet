package mchorse.mappet.utils;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import mchorse.mappet.api.states.States;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.entities.EntityNpc;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_3532;

public class EntityUtils {
   public static final Set<String> ENTITY_PROPERTIES = ImmutableSet.of("xp", "xp_level", "hp", "hunger", "armor", "ticks", new String[]{"light", "light_sky", "sneaking", "sprinting", "on_ground", "yaw", "pitch"});

   public static States getStates(class_1297 entity) {
      if (entity instanceof class_1657) {
         ICharacter character = Character.get((class_1657)entity);
         if (character != null) {
            return character.getStates();
         }
      } else if (entity instanceof EntityNpc) {
         return ((EntityNpc)entity).getStates();
      }

      return null;
   }

   public static double getProperty(class_1297 entity, String property) {
      class_1657 player = entity instanceof class_1657 ? (class_1657)entity : null;
      class_1309 living = entity instanceof class_1309 ? (class_1309)entity : null;
      switch (property) {
         case "xp" -> {
            return player == null ? (double)0.0F : (double)player.field_7495;
         }
         case "xp_level" -> {
            return player == null ? (double)0.0F : (double)player.field_7520;
         }
         case "hp" -> {
            return living == null ? (double)0.0F : (double)living.method_6032();
         }
         case "hunger" -> {
            return player == null ? (double)0.0F : (double)player.method_7344().method_7586();
         }
         case "armor" -> {
            return player == null ? (double)0.0F : (double)player.method_6096();
         }
         case "ticks" -> {
            return (double)entity.field_6012;
         }
         case "light" -> {
            return (double)((float)getCombinedLight(entity) % 65536.0F / 15.0F);
         }
         case "light_sky" -> {
            return (double)((float)getCombinedLight(entity) / 65536.0F / 15.0F);
         }
         case "sneaking" -> {
            return entity.method_5715() ? (double)1.0F : (double)0.0F;
         }
         case "sprinting" -> {
            return entity.method_5624() ? (double)1.0F : (double)0.0F;
         }
         case "on_ground" -> {
            return entity.method_24828() ? (double)1.0F : (double)0.0F;
         }
         case "yaw" -> {
            return (double)class_3532.method_15393(entity.method_36454());
         }
         case "pitch" -> {
            return (double)entity.method_36455();
         }
         default -> {
            return (double)0.0F;
         }
      }
   }

   public static int getCombinedLight(class_1297 entity) {
      class_2338.class_2339 pos = new class_2338.class_2339(class_3532.method_15357(entity.method_23317()), 0, class_3532.method_15357(entity.method_23321()));
      if (entity.method_37908().method_22340(pos)) {
         pos.method_33098(class_3532.method_15357(entity.method_23318() + (double)entity.method_5751()));
         return entity.method_37908().method_8314(class_1944.field_9282, pos) << 4 | entity.method_37908().method_8314(class_1944.field_9284, pos) << 20;
      } else {
         return 0;
      }
   }

   public static float getHeight(class_1297 entity) {
      if (entity instanceof IMorphProvider) {
         AbstractMorph morphProvider = ((IMorphProvider)entity).getMorph();
         if (entity instanceof class_1657) {
            return entity.method_5715() ? morphProvider.hitbox.sneakingHeight : morphProvider.hitbox.height;
         }
      }

      return entity.method_17682();
   }

   public static float getEyeHeight(class_1297 entity) {
      if (entity instanceof IMorphProvider) {
         AbstractMorph morphProvider = ((IMorphProvider)entity).getMorph();
         if (entity instanceof class_1657) {
            float height = getHeight(entity);
            float eyeHeight = height * morphProvider.hitbox.eye;
            float minEyeToHeadDifference = 0.1F;
            if (eyeHeight + minEyeToHeadDifference > height) {
               eyeHeight = height - minEyeToHeadDifference;
            }

            return eyeHeight;
         }
      }

      return entity.method_5751();
   }
}
