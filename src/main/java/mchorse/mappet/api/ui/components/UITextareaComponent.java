package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.client.gui.utils.text.GuiMultiTextElement;
import mchorse.mappet.client.gui.utils.text.TextLine;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class UITextareaComponent extends UILabelBaseComponent {
   public UITextareaComponent noBackground() {
      this.hasBackground = false;
      return this;
   }

   @DiscardMethod
   protected int getDefaultUpdateDelay() {
      return 200;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      if (key.equals("Label")) {
         ((GuiMultiTextElement)element).setText(this.label);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiMultiTextElement<TextLine> element = new GuiMultiTextElement<TextLine>(mc, (t) -> {
         if (!this.id.isEmpty()) {
            context.data.method_10582(this.id, t);
            context.dirty(this.id, (long)this.updateDelay);
         }

      });
      element.wrap().setText(this.label);
      element.background(this.hasBackground);
      return this.apply(element, context);
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      super.populateData(tag);
      if (!this.id.isEmpty()) {
         tag.method_10582(this.id, this.label);
      }

   }
}
