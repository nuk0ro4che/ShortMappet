package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;


public class UIColorComponent extends UIComponent {
   public int color = -1;

   public UIColorComponent color(int color) {
      this.change("Color");
      this.color = color;
      return this;
   }

   @DiscardMethod
   protected int getDefaultUpdateDelay() {
      return 100;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      if (key.equals("Color")) {
         ((GuiColorElement)element).picker.setValue(this.color);
      }
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiColorElement element = new GuiColorElement(mc, (color) -> {
         if (!this.id.isEmpty()) {
            context.data.method_10569(this.id, color);
            context.dirty(this.id, (long)this.updateDelay);
         }
      });
      element.picker.editAlpha();
      element.picker.setValue(this.color);
      return this.apply(element, context);
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      if (!this.id.isEmpty()) {
         tag.method_10569(this.id, this.color);
      }
   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10569("Color", this.color);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Color")) {
         this.color = tag.method_10550("Color");
      }
   }
}
