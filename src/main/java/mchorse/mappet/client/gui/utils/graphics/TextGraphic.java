package mchorse.mappet.client.gui.utils.graphics;

import java.util.Objects;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class TextGraphic extends Graphic {
   public String text;
   public float anchorX;
   public float anchorY;

   public TextGraphic() {
   }

   public TextGraphic(String text, int x, int y, int w, int h, int primary, float anchorX, float anchorY) {
      this.pixels.set(x, y, w, h);
      this.primary = primary;
      this.text = text;
      this.anchorX = anchorX;
      this.anchorY = anchorY;
   }

   @Environment(EnvType.CLIENT)
   public void drawGraphic(Area area) {
      class_310 mc = class_310.method_1551();
      int w = mc.field_1772.method_1727(this.text);
      int left = area.x(this.anchorX) - (int)((float)w * this.anchorX);
      int var10000 = area.y(this.anchorY);
      Objects.requireNonNull(mc.field_1772);
      int top = var10000 - (int)(9.0F * this.anchorY);
      GuiDraw.drawStringWithShadow(mc.field_1772, this.text, left, top, this.primary);
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("Text", this.text);
      tag.method_10548("AnchorX", this.anchorX);
      tag.method_10548("AnchorY", this.anchorY);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.text = tag.method_10558("Text");
      this.anchorX = tag.method_10583("AnchorX");
      this.anchorY = tag.method_10583("AnchorY");
   }
}
