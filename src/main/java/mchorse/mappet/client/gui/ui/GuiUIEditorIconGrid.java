package mchorse.mappet.client.gui.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.IconRegistry;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;


public class GuiUIEditorIconGrid extends GuiElement {
   private static final int CELL = 18;
   private final Consumer<String> callback;
   private final List<String> icons = new ArrayList();
   private int scroll;
   private String selected = "";

   public GuiUIEditorIconGrid(class_310 mc, Consumer<String> callback) {
      super(mc);
      this.callback = callback;
      this.icons.addAll(IconRegistry.icons.keySet());
      Collections.sort(this.icons);
   }

   public void setSelected(String id) {
      this.selected = id == null ? "" : id;
   }

   private int columns() {
      return Math.max(1, this.area.w / CELL);
   }

   private int visibleRows() {
      return Math.max(1, this.area.h / CELL);
   }

   private int maxScroll() {
      return Math.max(0, (int)Math.ceil((double)this.icons.size() / (double)this.columns()) - this.visibleRows());
   }

   public boolean mouseClicked(GuiContext context) {
      if (this.area.isInside(context) && context.mouseButton == 0) {
         int column = (context.mouseX - this.area.x) / CELL;
         int row = (context.mouseY - this.area.y) / CELL + this.scroll;
         int index = row * this.columns() + column;

         if (index >= 0 && index < this.icons.size()) {
            this.selected = (String)this.icons.get(index);
            this.callback.accept(this.selected);
            return true;
         }
      }

      return super.mouseClicked(context);
   }

   public boolean mouseScrolled(GuiContext context) {
      if (this.area.isInside(context) && context.mouseWheel != 0.0D) {
         this.scroll = Math.max(0, Math.min(this.maxScroll(), this.scroll + (int)Math.signum(context.mouseWheel)));
         return true;
      }

      return super.mouseScrolled(context);
   }

   public void draw(GuiContext context) {
      this.area.draw(-2013265920);
      GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
      int columns = this.columns();
      int start = this.scroll * columns;
      int end = Math.min(this.icons.size(), start + this.visibleRows() * columns);

      for(int index = start; index < end; ++index) {
         String id = (String)this.icons.get(index);
         int local = index - start;
         int x = this.area.x + local % columns * CELL + 1;
         int y = this.area.y + local / columns * CELL + 1;
         boolean hovered = context.mouseX >= x && context.mouseX < x + CELL - 2 && context.mouseY >= y && context.mouseY < y + CELL - 2;

         if (id.equals(this.selected)) {
            GuiDraw.drawRect(x - 1, y - 1, x + CELL - 1, y + CELL - 1, -16777216 | 0x4F8CFF);
         } else if (hovered) {
            GuiDraw.drawRect(x - 1, y - 1, x + CELL - 1, y + CELL - 1, -1610612736);
         }

         Icon icon = (Icon)IconRegistry.icons.get(id);
         if (icon != null) {
            icon.render(x + 7, y + 7, 0.5F, 0.5F);
         }
      }

      GuiDraw.unscissor(context);
      super.draw(context);
   }

   public void drawTooltip(GuiContext context, mchorse.mclib.client.gui.utils.Area viewport) {
      if (this.area.isInside(context)) {
         int column = (context.mouseX - this.area.x) / CELL;
         int row = (context.mouseY - this.area.y) / CELL + this.scroll;
         int index = row * this.columns() + column;
         if (index >= 0 && index < this.icons.size()) {
            this.tooltip(IKey.str((String)this.icons.get(index)));
         }
      }

      super.drawTooltip(context, viewport);
   }
}
