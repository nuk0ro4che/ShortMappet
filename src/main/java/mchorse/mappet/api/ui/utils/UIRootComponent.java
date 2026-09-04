package mchorse.mappet.api.ui.utils;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mappet.api.ui.components.UIParentComponent;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;

public class UIRootComponent extends UIParentComponent {
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      GuiElement element = new GuiElement(mc);

      for(UIComponent component : this.getChildComponents()) {
         GuiElement created = component.create(mc, context);
         created.flex().relative(element);
         element.add(created);
      }

      return this.applyKeybinds(element, context);
   }
}
