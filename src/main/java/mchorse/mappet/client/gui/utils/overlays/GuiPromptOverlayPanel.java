package mchorse.mappet.client.gui.utils.overlays;

import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiPromptOverlayPanel extends GuiOverlayPanel {
   public GuiTextElement text;

   public GuiPromptOverlayPanel(class_310 mc, IKey title, GuiTextElement element) {
      super(mc, title);
      this.text = new GuiTextElement(mc, element.field.getMaxStringLength(), (t) -> {
         element.field.setText(t);
         if (element.callback != null) {
            element.callback.accept(t);
         }

      });
      this.text.setText(element.field.getText());
      this.text.flex().relative(this.content).y(1.0F, -30).w(1.0F);
      this.content.add(this.text);
   }
}
