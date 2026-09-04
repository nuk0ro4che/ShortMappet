package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mappet.api.ui.utils.GuiClick;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_2494;
import net.minecraft.class_2499;
import net.minecraft.class_310;

public class UIClickComponent extends UIComponent {
   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      return this.apply(new GuiClick(mc, this, context), context);
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      super.populateData(tag);
      if (!this.id.isEmpty()) {
         class_2499 list = new class_2499();
         list.add(class_2494.method_23244(0.0F));
         list.add(class_2494.method_23244(0.0F));
         list.add(class_2494.method_23244(0.0F));
         list.add(class_2494.method_23244(0.0F));
         list.add(class_2494.method_23244(0.0F));
         tag.method_10566(this.id, list);
      }

   }
}
