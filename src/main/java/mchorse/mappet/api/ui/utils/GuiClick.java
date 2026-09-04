package mchorse.mappet.api.ui.utils;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2494;
import net.minecraft.class_2499;
import net.minecraft.class_310;

@Environment(EnvType.CLIENT)
public class GuiClick extends GuiElement {
   public UIComponent component;
   public UIContext context;

   public GuiClick(class_310 mc, UIComponent component, UIContext context) {
      super(mc);
      this.component = component;
      this.context = context;
   }

   public boolean mouseClicked(GuiContext context) {
      if (super.mouseClicked(context)) {
         return true;
      } else if (this.area.isInside(context) && !this.component.id.isEmpty()) {
         class_2499 list = new class_2499();
         list.add(class_2494.method_23244((float)(context.mouseX - this.area.x)));
         list.add(class_2494.method_23244((float)(context.mouseY - this.area.y)));
         list.add(class_2494.method_23244((float)(context.mouseX - this.area.x) / (float)this.area.w));
         list.add(class_2494.method_23244((float)(context.mouseY - this.area.y) / (float)this.area.h));
         list.add(class_2494.method_23244((float)context.mouseButton));
         this.context.data.method_10566(this.component.id, list);
         this.context.dirty(this.component.id, (long)this.component.updateDelay);
         return true;
      } else {
         return false;
      }
   }
}
