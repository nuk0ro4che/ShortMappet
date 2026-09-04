package mchorse.mappet.client.gui.ui;

import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;


public class GuiUIComponentPaletteButton extends GuiButtonElement {
   private final Icon icon;
   private final int iconColor;

   public GuiUIComponentPaletteButton(class_310 mc, IKey label, Icon icon, int iconColor, Consumer<GuiButtonElement> callback) {
      super(mc, label, callback);
      this.icon = icon;
      this.iconColor = iconColor;
      this.background(true);
   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (this.icon != null) {
         GuiDraw.bindColor(this.iconColor);
         this.icon.render(this.area.x + 8, this.area.my(), 0.5F, 0.5F);
         GuiDraw.bindColor(-1);
      }
   }
}
