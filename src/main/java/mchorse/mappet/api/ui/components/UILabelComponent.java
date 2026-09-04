package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;

public class UILabelComponent extends UILabelBaseComponent {
   public Integer background;
   public float anchorX;
   public float anchorY;

   public UILabelComponent background(int background) {
      this.change(new String[]{"Background"});
      this.background = background;
      return this;
   }

   public UILabelComponent labelAnchor(float anchor) {
      return this.labelAnchor(anchor, anchor);
   }

   public UILabelComponent labelAnchor(float anchorX, float anchorY) {
      this.change(new String[]{"AnchorX", "AnchorY"});
      this.anchorX = anchorX;
      this.anchorY = anchorY;
      return this;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiLabel label = Elements.label(IKey.str(this.getLabel()));
      if (this.background != null) {
         label.background(this.background);
      }

      label.anchor(this.anchorX, this.anchorY);
      return this.apply(label, context);
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      GuiLabel label = (GuiLabel)element;
      if (key.equals("Label")) {
         label.label = IKey.str(this.getLabel());
      } else if (key.equals("Background")) {
         label.background = this.background;
      } else if (key.equals("AnchorX")) {
         label.anchorX = this.anchorX;
      } else if (key.equals("AnchorY")) {
         label.anchorY = this.anchorY;
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      if (this.background != null) {
         tag.method_10569("Background", this.background);
      }

      tag.method_10548("AnchorX", this.anchorX);
      tag.method_10548("AnchorY", this.anchorY);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Background")) {
         this.background = tag.method_10550("Background");
      }

      if (tag.method_10545("AnchorX")) {
         this.anchorX = tag.method_10583("AnchorX");
      }

      if (tag.method_10545("AnchorY")) {
         this.anchorY = tag.method_10583("AnchorY");
      }

   }
}
