package mchorse.mappet.client.gui.ui;

import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;






public class GuiUIEditorToggleRow extends GuiElement {
   private static final int SWITCH_WIDTH = 30;
   private static final int SWITCH_HEIGHT = 14;
   private final IKey label;
   private final Consumer<Boolean> callback;
   private boolean toggled;

   public GuiUIEditorToggleRow(class_310 mc, IKey label, Consumer<Boolean> callback) {
      super(mc);
      this.label = label;
      this.callback = callback;
   }

   public boolean isToggled() {
      return this.toggled;
   }

   
   public GuiUIEditorToggleRow toggled(boolean toggled) {
      this.toggled = toggled;

      return this;
   }

   public boolean mouseClicked(GuiContext context) {
      if (this.area.isInside(context) && context.mouseButton == 0) {
         this.toggled = !this.toggled;
         this.callback.accept(this.toggled);

         return true;
      }

      return super.mouseClicked(context);
   }

   public void draw(GuiContext context) {
      int switchX = this.area.ex() - SWITCH_WIDTH;
      int switchY = this.area.y + Math.max(0, (this.area.h - SWITCH_HEIGHT) / 2);
      boolean hovered = this.area.isInside(context);
      int track = this.toggled ? 0xFF6E1408 : 0xFF303036;
      int border = this.toggled ? 0xFFC7513C : (hovered ? 0xFF999999 : 0xFF606066);
      int knob = this.toggled ? 0xFFFFFFFF : 0xFFB8B8BE;

      GuiDraw.drawStringWithShadow(this.font, this.label.get(), this.area.x, this.area.y + Math.max(0, (this.area.h - 8) / 2), 0xFFFFFF);
      GuiDraw.drawRect(switchX, switchY, switchX + SWITCH_WIDTH, switchY + SWITCH_HEIGHT, border);
      GuiDraw.drawRect(switchX + 1, switchY + 1, switchX + SWITCH_WIDTH - 1, switchY + SWITCH_HEIGHT - 1, track);

      int knobX = this.toggled ? switchX + SWITCH_WIDTH - SWITCH_HEIGHT : switchX + 2;
      GuiDraw.drawRect(knobX, switchY + 2, knobX + SWITCH_HEIGHT - 4, switchY + SWITCH_HEIGHT - 2, knob);
      super.draw(context);
   }
}
