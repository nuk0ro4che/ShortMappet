package mchorse.mappet.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.tooltips.ITooltip;
import mchorse.mclib.client.gui.framework.tooltips.styles.TooltipStyle;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

@Environment(EnvType.CLIENT)
public class UIMorphTooltip implements ITooltip {
   private final GuiMorphRenderer renderer;
   private final String text;
   private final int width;
   private final int height;
   private final int direction;

   public UIMorphTooltip(class_310 mc, AbstractMorph morph, String text) {
      this(mc, morph, text, 150, 142, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0);
   }

   public UIMorphTooltip(class_310 mc, AbstractMorph morph, String text, int width, int height, float scale, float yaw, float pitch, float offsetX, float offsetY, float offsetZ, int direction) {
      this.renderer = new GuiMorphRenderer(mc);
      this.renderer.morph.set(MorphUtils.copy(morph));
      this.renderer.scale = Math.max(0.05F, scale);
      this.renderer.fov = 45.0F;
      this.renderer.setRotation(yaw, pitch);
      this.renderer.setPosition(offsetX, offsetY, offsetZ);
      this.text = text == null ? "" : text;
      this.width = Math.max(32, width);
      this.height = Math.max(48, height);
      this.direction = Math.max(0, Math.min(3, direction));
   }

   public void drawTooltip(GuiContext context) {
      if (context.tooltip.element == null) {
         return;
      }

      Area source = context.tooltip.element.area;
      int width = Math.max(this.width, GuiDraw.textWidth(context.font, this.text) + 12);
      int height = this.height;
      int x = source.mx() - width / 2;
      int y = source.y - height - 6;
      if (this.direction == 1) {
         y = source.ey() + 6;
      } else if (this.direction == 2) {
         x = source.ex() + 6;
         y = source.my() - height / 2;
      } else if (this.direction == 3) {
         x = source.x - width - 6;
         y = source.my() - height / 2;
      }
      int screenWidth = context.screen.field_22789;
      int screenHeight = context.screen.field_22790;

      if (x < 3) {
         x = 3;
      }
      if (x + width > screenWidth - 3) {
         x = screenWidth - width - 3;
      }
      if (y < 3) {
         y = source.ey() + 6;
      }
      if (y + height > screenHeight - 3) {
         y = screenHeight - height - 3;
      }

      context.tooltip.area.set(x, y, width, height);
      TooltipStyle style = TooltipStyle.get();
      style.drawBackground(context.tooltip.area);

      this.renderer.area.set(x + 4, y + 4, width - 8, height - 28);
      this.renderer.draw(context);

      if (!this.text.isEmpty()) {
         GuiDraw.drawString(context.font, this.text, x + 6, y + height - 18, style.getTextColor());
      }
   }
}
