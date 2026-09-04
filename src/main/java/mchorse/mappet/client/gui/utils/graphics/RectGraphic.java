package mchorse.mappet.client.gui.utils.graphics;

import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class RectGraphic extends Graphic {
   public RectGraphic() {
   }

   public RectGraphic(int x, int y, int w, int h, int primary) {
      this.pixels.set(x, y, w, h);
      this.primary = primary;
   }

   @Environment(EnvType.CLIENT)
   public void drawGraphic(Area area) {
      GuiDraw.drawRect(area.x, area.y, area.ex(), area.ey(), this.primary);
   }
}
