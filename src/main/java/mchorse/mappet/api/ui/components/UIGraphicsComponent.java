package mchorse.mappet.api.ui.components;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.client.gui.utils.GuiGraphics;
import mchorse.mappet.client.gui.utils.graphics.GradientGraphic;
import mchorse.mappet.client.gui.utils.graphics.Graphic;
import mchorse.mappet.client.gui.utils.graphics.IconGraphic;
import mchorse.mappet.client.gui.utils.graphics.ImageGraphic;
import mchorse.mappet.client.gui.utils.graphics.RectGraphic;
import mchorse.mappet.client.gui.utils.graphics.ShadowGraphic;
import mchorse.mappet.client.gui.utils.graphics.TextGraphic;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.utils.resources.RLUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_310;

public class UIGraphicsComponent extends UIComponent {
   public List<Graphic> graphics = new ArrayList();

   public UIGraphicsComponent removeAll() {
      this.change(new String[]{"Graphics"});
      this.graphics.clear();
      return this;
   }

   public Graphic rect(int color) {
      return this.rect(0, 0, 0, 0, color);
   }

   public Graphic rect(int x, int y, int w, int h, int color) {
      return this.addGraphic(new RectGraphic(x, y, w, h, color));
   }

   public Graphic gradient(int primary, int secondary) {
      return this.gradient(primary, secondary, false);
   }

   public Graphic gradient(int primary, int secondary, boolean horizontal) {
      return this.gradient(0, 0, 0, 0, primary, secondary, horizontal);
   }

   public Graphic gradient(int x, int y, int w, int h, int primary, int secondary) {
      return this.gradient(x, y, w, h, primary, secondary, false);
   }

   public Graphic gradient(int x, int y, int w, int h, int primary, int secondary, boolean horizontal) {
      return this.addGraphic(new GradientGraphic(x, y, w, h, primary, secondary, horizontal));
   }

   public Graphic image(String image, int textureWidth, int textureHeight) {
      return this.image(image, textureWidth, textureHeight, -1);
   }

   public Graphic image(String image, int textureWidth, int textureHeight, int primary) {
      return this.image(image, 0, 0, 0, 0, textureWidth, textureHeight, primary);
   }

   public Graphic image(String image, int x, int y, int w, int h) {
      return this.image(image, x, y, w, h, w, h, -1);
   }

   public Graphic image(String image, int x, int y, int w, int h, int textureWidth, int textureHeight) {
      return this.image(image, x, y, w, h, textureWidth, textureHeight, -1);
   }

   public Graphic image(String image, int x, int y, int w, int h, int textureWidth, int textureHeight, int primary) {
      return this.addGraphic(new ImageGraphic(RLUtils.create(image), x, y, w, h, textureWidth, textureHeight, primary));
   }

   public Graphic text(String text, int x, int y, int color) {
      return this.text(text, x, y, color, 0.0F, 0.0F);
   }

   public Graphic text(String text, int x, int y, int color, float anchorX, float anchorY) {
      return this.text(text, x, y, 0, 0, color, anchorX, anchorY);
   }

   public Graphic text(String text, int x, int y, int w, int h, int color, float anchorX, float anchorY) {
      return this.addGraphic(new TextGraphic(text, x, y, w, h, color, anchorX, anchorY));
   }

   public Graphic icon(String icon, int x, int y, int color) {
      return this.icon(icon, x, y, color, 0.0F, 0.0F);
   }

   public Graphic icon(String icon, int x, int y, int color, float anchorX, float anchorY) {
      return this.addGraphic(new IconGraphic(icon, x, y, color, anchorX, anchorY));
   }

   public Graphic shadow(int primary, int secondary, int offset) {
      return this.shadow(0, 0, 0, 0, primary, secondary, offset);
   }

   public Graphic shadow(int x, int y, int w, int h, int primary, int secondary, int offset) {
      return this.addGraphic(new ShadowGraphic(x, y, w, h, primary, secondary, offset));
   }

   @DiscardMethod
   private <T extends Graphic> T addGraphic(T graphic) {
      this.change(new String[]{"Graphics"});
      this.graphics.add(graphic);
      return graphic;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiGraphics element = new GuiGraphics(mc);
      element.graphics.addAll(this.graphics);
      return this.apply(element, context);
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      if (key.equals("Graphics")) {
         GuiGraphics graphics = (GuiGraphics)element;
         graphics.graphics.clear();
         graphics.graphics.addAll(this.graphics);
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      class_2499 list = new class_2499();

      for(Graphic graphic : this.graphics) {
         list.add(Graphic.toNBT(graphic));
      }

      tag.method_10566("Graphics", list);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Graphics")) {
         class_2499 list = tag.method_10554("Graphics", 10);
         this.graphics.clear();
         int i = 0;

         for(int c = list.size(); i < c; ++i) {
            Graphic graphic = Graphic.fromNBT(list.method_10602(i));
            if (graphic != null) {
               this.graphics.add(graphic);
            }
         }
      }

   }
}
