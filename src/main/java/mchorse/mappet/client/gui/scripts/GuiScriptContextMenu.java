package mchorse.mappet.client.gui.scripts;

import java.util.function.Supplier;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;
import net.minecraft.class_327;

public class GuiScriptContextMenu extends GuiSimpleContextMenu {
   private static final int BORDER = 0xFF50545A;
   private static final int HOVER = 0xFF30343A;
   private static final int SELECTED = 0xFF214B8F;
   private static final int ACCENT = 0xFF8AB4F8;

   public GuiScriptContextMenu(class_310 mc) {
      super(mc);
      this.actions.scroll.scrollItemSize = 22;
      this.actions.background(false, 0);
   }

   public GuiScriptContextMenu action(Icon icon, IKey label, Runnable runnable) {
      if (icon != null && label != null) {
         this.actions.add(new DarkAction(icon, label, runnable));
      }

      return this;
   }

   public void setMouse(GuiContext context) {
      int width = 150;

      for(Action action : this.actions.getList()) {
         width = Math.max(width, action.getWidth(this.font));
      }

      Supplier<Float> height = () -> (float)Math.min(this.actions.scroll.scrollSize, context.screen.root.area.h - 10);
      this.flex().set(context.mouseX(), context.mouseY(), width, 0.0F).h(height).bounds(context.screen.root, 5);
   }

   public void draw(GuiContext context) {
      GuiDraw.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 0xFF0F1014);
      this.actions.draw(context);
      GuiDraw.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.y + 1, BORDER);
      GuiDraw.drawRect(this.area.x, this.area.ey() - 1, this.area.ex(), this.area.ey(), BORDER);
      GuiDraw.drawRect(this.area.x, this.area.y, this.area.x + 1, this.area.ey(), BORDER);
      GuiDraw.drawRect(this.area.ex() - 1, this.area.y, this.area.ex(), this.area.ey(), BORDER);
   }

   private static class DarkAction extends Action {
      public DarkAction(Icon icon, IKey label, Runnable runnable) {
         super(icon, label, runnable);
      }

      public int getWidth(class_327 font) {
         return 34 + GuiDraw.textWidth(font, this.label.get());
      }

      public void draw(class_327 font, int x, int y, int width, int height, boolean hover, boolean selected) {
         if (selected) {
            GuiDraw.drawRect(x, y, x + width, y + height, SELECTED);
            GuiDraw.drawRect(x, y, x + 2, y + height, ACCENT);
         } else if (hover) {
            GuiDraw.drawRect(x, y, x + width, y + height, HOVER);
         }

         GuiDraw.resetColor();
         this.icon.render(x + 6, y + height / 2, 0, 0.5F);
         GuiDraw.drawString(font, this.label.get(), x + 27, y + (height - GuiDraw.fontHeight(font)) / 2 + 1, 0xFFFFFF);
      }
   }
}
