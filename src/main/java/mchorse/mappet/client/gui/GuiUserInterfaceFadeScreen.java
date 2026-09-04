package mchorse.mappet.client.gui;

import mchorse.mclib.utils.Interpolation;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class GuiUserInterfaceFadeScreen extends class_437 {
   private final int duration;
   private final Interpolation interpolation;
   private long startedAt = -1L;

   public GuiUserInterfaceFadeScreen(class_310 mc, int duration, Interpolation interpolation) {
      super(class_2561.method_43470(""));
      this.duration = Math.max(0, duration);
      this.interpolation = interpolation;
   }

   public boolean method_25421() {
      return false;
   }

   public boolean method_25422() {
      return false;
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      if (this.startedAt < 0L) {
         this.startedAt = System.nanoTime();
      }

      float factor = this.getFactor();
      int alpha = Math.max(0, Math.min(255, Math.round(this.interpolation.interpolate(255.0F, 0.0F, factor))));
      if (alpha > 0) {
         drawContext.method_25294(0, 0, this.field_22789, this.field_22790, alpha << 24);
      }

      if (factor >= 1.0F) {
         this.field_22787.method_1507((class_437)null);
      }
   }

   private float getFactor() {
      if (this.duration <= 0) {
         return 1.0F;
      }

      float elapsed = (float)(System.nanoTime() - this.startedAt) / 5.0E7F;
      return Math.min(1.0F, Math.max(0.0F, elapsed / (float)this.duration));
   }
}
