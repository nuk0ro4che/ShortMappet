package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.ITextColoring;
import mchorse.mclib.utils.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public abstract class UILabelBaseComponent extends UIComponent {
   public String label = "";
   private Integer color;
   private boolean textShadow = true;
   protected boolean hasBackground = true;

   public UILabelBaseComponent color(int color) {
      return this.color(color, true);
   }

   public UILabelBaseComponent color(int color, boolean shadow) {
      this.change(new String[]{"Color", "TextShadow"});
      this.color = color;
      this.textShadow = shadow;
      return this;
   }

   public UILabelBaseComponent label(String label) {
      this.change(new String[]{"Label"});
      this.label = label;
      return this;
   }

   @DiscardMethod
   protected String getLabel() {
      return TextUtils.processColoredText(this.label);
   }

   @Environment(EnvType.CLIENT)
   protected GuiElement apply(GuiElement element, UIContext context) {
      if (element instanceof ITextColoring && this.color != null) {
         ((ITextColoring)element).setColor(this.color, this.textShadow);
      }

      return super.apply(element, context);
   }

   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      if (key.equals("Color") && element instanceof ITextColoring) {
         ((ITextColoring)element).setColor(this.color, this.textShadow);
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("Label", this.label);
      if (this.color != null) {
         tag.method_10569("Color", this.color);
      }

      tag.method_10556("TextShadow", this.textShadow);
      tag.method_10556("HasBackground", this.hasBackground);
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Label")) {
         this.label = tag.method_10558("Label");
      }

      if (tag.method_10545("Color")) {
         this.color = tag.method_10550("Color");
      }

      if (tag.method_10545("TextShadow")) {
         this.textShadow = tag.method_10577("TextShadow");
      }

      if (tag.method_10545("HasBackground")) {
         this.hasBackground = tag.method_10577("HasBackground");
      }

   }
}
