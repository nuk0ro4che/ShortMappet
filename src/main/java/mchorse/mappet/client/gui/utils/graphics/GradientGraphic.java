package mchorse.mappet.client.gui.utils.graphics;

import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class GradientGraphic extends Graphic {
   public int secondary;
   public boolean horizontal;

   public GradientGraphic() {
   }

   public GradientGraphic(int x, int y, int w, int h, int primary, int secondary, boolean horizontal) {
      this.pixels.set(x, y, w, h);
      this.primary = primary;
      this.secondary = secondary;
      this.horizontal = horizontal;
   }

   @Environment(EnvType.CLIENT)
   protected void drawGraphic(Area area) {
      if (this.horizontal) {
         GuiDraw.drawHorizontalGradientRect(area.x, area.y, area.ex(), area.ey(), this.primary, this.secondary);
      } else {
         GuiDraw.drawVerticalGradientRect(area.x, area.y, area.ex(), area.ey(), this.primary, this.secondary);
      }

   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10569("Secondary", this.secondary);
      tag.method_10556("Horizontal", this.horizontal);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.secondary = tag.method_10550("Secondary");
      this.horizontal = tag.method_10577("Horizontal");
   }
}
