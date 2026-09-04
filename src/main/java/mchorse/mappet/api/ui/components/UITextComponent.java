package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class UITextComponent extends UILabelBaseComponent {
   public float textAnchor;

   public UITextComponent textAnchor(float anchor) {
      this.textAnchor = anchor;
      return this;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      return this.apply((new GuiText(mc)).text(this.getLabel()).anchorX(this.textAnchor), context);
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      if (key.equals("Label")) {
         ((GuiText)element).text(this.getLabel());
      } else if (key.equals("TextAnchor")) {
         ((GuiText)element).anchorX(this.textAnchor);
      }

   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10548("TextAnchor", this.textAnchor);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("TextAnchor")) {
         this.textAnchor = tag.method_10583("TextAnchor");
      }

   }
}
