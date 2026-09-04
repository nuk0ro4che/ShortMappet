package mchorse.mappet.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_742;

@Environment(EnvType.CLIENT)
public final class FirstPersonBodyRenderer {
   private static boolean rendering;

   private FirstPersonBodyRenderer() {
   }

   public static boolean isRendering() {
      return rendering;
   }

   public static void render(class_742 player, float tickDelta, class_4587 matrices, class_4597 consumers, int light) {
      if (!FirstPersonBodyState.isEnabled() || rendering || player == null) {
         return;
      }

      rendering = true;

      try {
         float yaw = player.method_5705(tickDelta);
         class_310.method_1551().method_1561().method_3954(player, 0.0D, -1.62D, 0.0D, yaw, tickDelta, matrices, consumers, light);
      } finally {
         rendering = false;
      }
   }
}
