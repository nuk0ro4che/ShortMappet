package mchorse.mappet.client.gui.utils;

import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Area;
import net.minecraft.class_1799;
import net.minecraft.class_310;

public class GuiUserInterfaceSlotElement extends GuiSlotElement {
   public GuiUserInterfaceSlotElement(class_310 mc, int slot, Consumer<class_1799> callback) {
      super(mc, slot, callback);
   }

   public void drawTooltip(GuiContext context, Area area) {
      context.tooltip.draw(this.tooltip, context);
   }
}
