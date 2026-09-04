package mchorse.mappet.client.gui.utils.graphics;

import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.IconRegistry;
import mchorse.mclib.client.gui.utils.Icons;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class IconGraphic extends Graphic {
   public String id;
   public float anchorX;
   public float anchorY;
   @Environment(EnvType.CLIENT)
   private Icon icon;

   public IconGraphic() {
   }

   public IconGraphic(String id, int x, int y, int primary, float anchorX, float anchorY) {
      this.pixels.set(x - 8, y - 8, 16, 16);
      this.primary = primary;
      this.id = id;
      this.anchorX = anchorX;
      this.anchorY = anchorY;
   }

   public IconGraphic icon(String id) {
      this.id = id == null ? "" : id;
      this.icon = null;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public void drawGraphic(Area area) {
      GuiDraw.bindColor(this.primary);
      if (this.icon == null) {
         this.icon = (Icon)IconRegistry.icons.get(this.id);
         this.icon = this.icon == null ? Icons.NONE : this.icon;
      }

      int left = area.x(this.anchorX);
      int top = area.y(this.anchorY);
      this.icon.render(left, top, this.anchorX, this.anchorY);
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("Icon", this.id);
      tag.method_10548("AnchorX", this.anchorX);
      tag.method_10548("AnchorY", this.anchorY);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.id = tag.method_10558("Icon");
      this.anchorX = tag.method_10583("AnchorX");
      this.anchorY = tag.method_10583("AnchorY");
   }
}
