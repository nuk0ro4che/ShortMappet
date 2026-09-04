package mchorse.mappet.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.GuiScrollElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.class_310;

public class GuiScrollLogsElement extends GuiScrollElement {
   public boolean background = false;

   public GuiScrollLogsElement(class_310 mc) {
      super(mc);
   }

   public GuiScrollLogsElement background() {
      this.background = true;
      return this;
   }

   public void draw(GuiContext context) {
      if (this.background) {
         this.area.draw(2013265920);
      }

      super.draw(context);
   }
}
