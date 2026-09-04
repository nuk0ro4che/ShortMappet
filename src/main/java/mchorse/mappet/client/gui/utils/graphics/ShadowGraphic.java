package mchorse.mappet.client.gui.utils.graphics;

import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraft.class_2487;

public class ShadowGraphic extends Graphic {
   public int secondary;
   public int offset;

   public ShadowGraphic() {
   }

   public ShadowGraphic(int x, int y, int w, int h, int primary, int secondary, int offset) {
      this.pixels.set(x, y, w, h);
      this.primary = primary;
      this.secondary = secondary;
      this.offset = offset;
   }

   protected void drawGraphic(Area area) {
      GuiDraw.drawDropShadow(area.x, area.y, area.ex(), area.ey(), this.offset, this.primary, this.secondary);
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10569("Secondary", this.secondary);
      tag.method_10569("Offset", this.offset);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.secondary = tag.method_10550("Secondary");
      this.offset = tag.method_10550("Offset");
   }
}
