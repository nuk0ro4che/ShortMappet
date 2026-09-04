package mchorse.mappet.client.gui.utils;

import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiTextLabeledElement extends GuiTextElement {
   public IKey label;

   public GuiTextLabeledElement(class_310 mc, Consumer<String> callback) {
      super(mc, callback);
      this.label = IKey.EMPTY;
   }

   public GuiTextLabeledElement label(IKey label) {
      this.label = label;
      return this;
   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (!this.field.isFocused() && this.field.getText().isEmpty()) {
         GuiDraw.drawStringWithShadow(this.font, this.label.get(), this.area.x + 5, this.area.y + 6, 8947848);
      }

   }
}
