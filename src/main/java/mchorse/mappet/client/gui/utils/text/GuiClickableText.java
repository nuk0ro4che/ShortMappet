package mchorse.mappet.client.gui.utils.text;

import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.class_310;

public class GuiClickableText extends GuiText {
   private Consumer<GuiClickableText> callback;

   public GuiClickableText(class_310 mc) {
      super(mc);
   }

   public GuiClickableText callback(Consumer<GuiClickableText> callback) {
      this.callback = callback;
      return this;
   }

   public boolean mouseClicked(GuiContext context) {
      if (super.mouseClicked(context)) {
         return true;
      } else if (context.mouseButton == 0 && this.area.isInside(context)) {
         if (this.callback != null) {
            this.callback.accept(this);
         }

         return true;
      } else {
         return false;
      }
   }
}
