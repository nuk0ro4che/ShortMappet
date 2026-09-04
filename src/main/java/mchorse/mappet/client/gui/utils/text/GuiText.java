package mchorse.mappet.client.gui.utils.text;

import java.util.List;
import java.util.Objects;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.ITextColoring;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;
import net.minecraft.class_327;

public class GuiText extends GuiElement implements ITextColoring {
   private IKey temp;
   private List<String> text;
   private int lineHeight;
   private int color;
   private int hoverColor;
   private boolean shadow;
   private int paddingH;
   private int paddingV;
   private float anchorX;
   private int lines;

   public GuiText(class_310 mc) {
      super(mc);
      this.temp = IKey.EMPTY;
      this.lineHeight = 12;
      this.color = 16777215;
      this.hoverColor = 16777215;
      this.shadow = true;
      this.flex().h(() -> (float)this.height());
   }

   private int height() {
      int var10000 = Math.max(this.lines, 1) * this.lineHeight;
      int var10001 = this.lineHeight;
      Objects.requireNonNull(this.font);
      int height = var10000 - (var10001 - 9);
      return height + this.paddingV * 2;
   }

   public IKey getText() {
      return this.temp;
   }

   public GuiText text(String text) {
      return this.text(IKey.str(text));
   }

   public GuiText text(IKey text) {
      this.temp = text;
      this.text = null;
      this.lines = 0;
      return this;
   }

   public GuiText lineHeight(int lineHeight) {
      this.lineHeight = lineHeight;
      return this;
   }

   public GuiText color(int color, boolean shadow) {
      this.color = this.hoverColor = color;
      this.shadow = shadow;
      return this;
   }

   public GuiText hoverColor(int color) {
      this.hoverColor = color;
      return this;
   }

   public GuiText padding(int padding) {
      return this.padding(padding, padding);
   }

   public GuiText padding(int horizontal, int vertical) {
      this.paddingH = horizontal;
      this.paddingV = vertical;
      return this;
   }

   public GuiText anchorX(float anchor) {
      this.anchorX = anchor;
      return this;
   }

   public void setColor(int color, boolean shadow) {
      this.color(color, shadow);
   }

   public void resize() {
      super.resize();
      this.text = null;
   }

   public void draw(GuiContext context) {
      if (this.area.w > 0) {
         if (this.text == null) {
            String var10000 = this.temp.get().replace("\\n", "\n");
            int var10001 = this.area.w - this.paddingH * 2;
            class_327 var10002 = this.font;
            Objects.requireNonNull(var10002);
            List<String> text = GuiDraw.listFormattedStringToWidth(var10000, var10001, var10002::method_1727);
            this.lines = text.size();
            this.getParentContainer().resize();
            this.text = text;
            this.lines = text.size();
         }

         int y = this.paddingV;
         int color = this.area.isInside(context) ? this.hoverColor : this.color;

         for(String line : this.text) {
            int x = this.area.x + this.paddingH;
            if (this.anchorX != 0.0F) {
               x += (int)((float)(this.area.w - this.paddingH * 2 - this.font.method_1727(line)) * this.anchorX);
            }

            if (this.shadow) {
               GuiDraw.drawStringWithShadow(this.font, line, x, this.area.y + y, color);
            } else {
               GuiDraw.drawString(this.font, line, x, this.area.y + y, color);
            }

            y += this.lineHeight;
         }
      }

      super.draw(context);
   }
}
