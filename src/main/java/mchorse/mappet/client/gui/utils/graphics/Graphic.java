package mchorse.mappet.client.gui.utils.graphics;

import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public abstract class Graphic implements INBTSerializable<class_2487> {
   private static Area computed = new Area();
   public Area pixels = new Area();
   public float relativeX;
   public float relativeY;
   public float relativeW;
   public float relativeH;
   public float anchorX;
   public float anchorY;
   public int primary;
   public boolean hover;

   @DiscardMethod
   public static Graphic fromNBT(class_2487 tag) {
      String type = tag.method_10558("Type");
      Graphic graphic = null;
      if (type.equals("rect")) {
         graphic = new RectGraphic();
      } else if (type.equals("gradient")) {
         graphic = new GradientGraphic();
      } else if (type.equals("image")) {
         graphic = new ImageGraphic();
      } else if (type.equals("text")) {
         graphic = new TextGraphic();
      } else if (type.equals("icon")) {
         graphic = new IconGraphic();
      } else if (type.equals("shadow")) {
         graphic = new ShadowGraphic();
      }

      if (graphic != null) {
         graphic.deserializeNBT(tag);
      }

      return graphic;
   }

   @DiscardMethod
   public static class_2487 toNBT(Graphic graphic) {
      class_2487 tag = graphic.serializeNBT();
      String type = "rect";
      if (graphic instanceof GradientGraphic) {
         type = "gradient";
      } else if (graphic instanceof ImageGraphic) {
         type = "image";
      } else if (graphic instanceof TextGraphic) {
         type = "text";
      } else if (graphic instanceof IconGraphic) {
         type = "icon";
      } else if (graphic instanceof ShadowGraphic) {
         type = "shadow";
      }

      tag.method_10582("Type", type);
      return tag;
   }

   public Graphic x(int value) {
      this.relativeX = 0.0F;
      this.pixels.x = value;
      return this;
   }

   public Graphic rx(float value) {
      return this.rx(value, 0);
   }

   public Graphic rx(float value, int offset) {
      this.relativeX = value;
      this.pixels.x = offset;
      return this;
   }

   public Graphic y(int value) {
      this.relativeY = 0.0F;
      this.pixels.y = value;
      return this;
   }

   public Graphic ry(float value) {
      return this.ry(value, 0);
   }

   public Graphic ry(float value, int offset) {
      this.relativeY = value;
      this.pixels.y = offset;
      return this;
   }

   public Graphic w(int value) {
      this.relativeW = 0.0F;
      this.pixels.w = value;
      return this;
   }

   public Graphic rw(float value) {
      return this.rw(value, 0);
   }

   public Graphic rw(float value, int offset) {
      this.relativeW = value;
      this.pixels.w = offset;
      return this;
   }

   public Graphic h(int value) {
      this.relativeH = 0.0F;
      this.pixels.h = value;
      return this;
   }

   public Graphic rh(float value) {
      return this.rh(value, 0);
   }

   public Graphic rh(float value, int offset) {
      this.relativeH = value;
      this.pixels.h = offset;
      return this;
   }

   public Graphic xy(int x, int y) {
      return this.x(x).y(y);
   }

   public Graphic rxy(float x, float y) {
      return this.rx(x).ry(y);
   }

   public Graphic wh(int w, int h) {
      return this.w(w).h(h);
   }

   public Graphic rwh(float w, float h) {
      return this.rw(w).rh(h);
   }

   public Graphic anchor(float anchor) {
      return this.anchor(anchor, this.anchorY);
   }

   public Graphic anchor(float x, float y) {
      return this.anchorX(x).anchorY(y);
   }

   public Graphic anchorX(float x) {
      this.anchorX = x;
      return this;
   }

   public Graphic anchorY(float y) {
      this.anchorY = y;
      return this;
   }

   public Graphic hoverOnly() {
      this.hover = true;
      return this;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public final void draw(GuiContext context, Area elementArea) {
      computed.x = elementArea.x + (int)((float)elementArea.w * this.relativeX) + this.pixels.x;
      computed.y = elementArea.y + (int)((float)elementArea.h * this.relativeY) + this.pixels.y;
      computed.w = (int)((float)elementArea.w * this.relativeW) + this.pixels.w;
      computed.h = (int)((float)elementArea.h * this.relativeH) + this.pixels.h;
      Area var10000 = computed;
      var10000.x = (int)((float)var10000.x - (float)computed.w * this.anchorX);
      var10000 = computed;
      var10000.y = (int)((float)var10000.y - (float)computed.h * this.anchorY);
      if (!this.hover || computed.isInside(context)) {
         this.drawGraphic(computed);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected abstract void drawGraphic(Area var1);

   @DiscardMethod
   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      this.serializeNBT(tag);
      return tag;
   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      tag.method_10569("X", this.pixels.x);
      tag.method_10569("Y", this.pixels.y);
      tag.method_10569("W", this.pixels.w);
      tag.method_10569("H", this.pixels.h);
      tag.method_10548("RX", this.relativeX);
      tag.method_10548("RY", this.relativeY);
      tag.method_10548("RW", this.relativeW);
      tag.method_10548("RH", this.relativeH);
      tag.method_10548("AX", this.anchorX);
      tag.method_10548("AY", this.anchorY);
      tag.method_10569("Primary", this.primary);
      tag.method_10556("Hover", this.hover);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      this.pixels.x = tag.method_10550("X");
      this.pixels.y = tag.method_10550("Y");
      this.pixels.w = tag.method_10550("W");
      this.pixels.h = tag.method_10550("H");
      this.relativeX = tag.method_10583("RX");
      this.relativeY = tag.method_10583("RY");
      this.relativeW = tag.method_10583("RW");
      this.relativeH = tag.method_10583("RH");
      this.anchorX = tag.method_10583("AX");
      this.anchorY = tag.method_10583("AY");
      this.primary = tag.method_10550("Primary");
      this.hover = tag.method_10577("Hover");
   }
}
