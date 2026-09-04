package mchorse.mappet.client.gui.utils;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.client.gui.utils.graphics.Graphic;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.class_310;

public class GuiGraphics extends GuiElement {
   public List<Graphic> graphics = new ArrayList();

   public GuiGraphics(class_310 mc) {
      super(mc);
   }

   public void draw(GuiContext context) {
      for(Graphic graphic : this.graphics) {
         graphic.draw(context, this.area);
      }

      super.draw(context);
   }
}
