package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class UITextboxComponent extends UILabelBaseComponent {
   public int maxLength = 32;

   public UITextboxComponent maxLength(int maxLength) {
      this.change(new String[]{"MaxLength"});
      this.maxLength = maxLength;
      return this;
   }

   public UITextboxComponent noBackground() {
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
         ((GuiTextElement)element).setText(this.label);
      } else if (key.equals("MaxLength")) {
         ((GuiTextElement)element).field.setMaxStringLength(this.maxLength);
      }

   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiTextElement element = new GuiTextElement(mc, this.maxLength, (t) -> {
         if (!this.id.isEmpty()) {
            context.data.method_10582(this.id, t);
            context.dirty(this.id, (long)this.updateDelay);
         }

      });
      element.setText(this.label);
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

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10569("MaxLength", this.maxLength);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("MaxLength")) {
         this.maxLength = tag.method_10550("MaxLength");
      }

   }
}
