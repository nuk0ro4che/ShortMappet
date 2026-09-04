package mchorse.mappet.client.gui.panels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.class_310;


public class GuiScriptTabBar extends GuiElement {
   private static final int HEIGHT = 22;
   private static final int MIN_WIDTH = 76;
   private static final int MAX_WIDTH = 156;
   private static final int CLOSE_WIDTH = 24;
   private static final int DRAG_THRESHOLD = 4;
   private final List<String> tabs = new ArrayList();
   private String active;
   private String dragging;
   private int dragStartX;
   private int dragMouseOffset;
   private Consumer<String> selectCallback;
   private Consumer<String> closeCallback;
   private BiConsumer<String, Integer> reorderCallback;

   public GuiScriptTabBar(class_310 mc) {
      super(mc);
   }

   public GuiScriptTabBar onSelect(Consumer<String> callback) {
      this.selectCallback = callback;
      return this;
   }

   public GuiScriptTabBar onClose(Consumer<String> callback) {
      this.closeCallback = callback;
      return this;
   }

   public GuiScriptTabBar onReorder(BiConsumer<String, Integer> callback) {
      this.reorderCallback = callback;
      return this;
   }

   public void setTabs(List<String> tabs, String active) {
      this.tabs.clear();
      if (tabs != null) {
         this.tabs.addAll(tabs);
      }
      this.active = active;
      if (this.dragging != null && !this.tabs.contains(this.dragging)) {
         this.dragging = null;
      }
   }

   public List<String> getTabs() {
      return Collections.unmodifiableList(this.tabs);
   }

   public void draw(GuiContext context) {
      super.draw(context);
      this.area.draw(-15724528);
      boolean visualDrag = this.isVisualDrag(context);

      if (!visualDrag) {
         int x = this.area.x;

         for (String tab : this.tabs) {
            int width = this.getTabWidth(tab);
            this.drawTab(context, tab, x, false);
            x += width;
            if (x >= this.area.ex()) {
               break;
            }
         }

         return;
      }

      int draggedWidth = this.getTabWidth(this.dragging);
      int target = this.getDragTargetIndex(context);
      int x = this.area.x;
      int remaining = 0;

      for (String tab : this.tabs) {
         if (tab.equals(this.dragging)) {
            continue;
         }

         if (remaining == target) {
            x += draggedWidth;
         }

         int width = this.getTabWidth(tab);
         this.drawTab(context, tab, x, true);
         x += width;
         remaining++;
      }

      this.drawTab(context, this.dragging, this.getDraggedTabLeft(context), true);
   }

   
   public int drawDetachedTab(String tab, int x, int y) {
      int width = this.getTabWidth(tab);
      GuiDraw.drawRect(x, y, x + width - 1, y + HEIGHT, -13487566);
      GuiDraw.drawRect(x, y, x + width - 1, y + 2, -10053172);
      String label = this.getLabel(tab, width - CLOSE_WIDTH - 12);
      GuiDraw.drawString(this.font, label, x + 8, y + 7, -1, false);
      return width;
   }

   private void drawTab(GuiContext context, String tab, int x, boolean draggingLayout) {
      int width = this.getTabWidth(tab);
      boolean selected = tab.equals(this.active);
      boolean draggedTab = tab.equals(this.dragging) && draggingLayout;
      int background = selected || draggedTab ? -13487566 : -15132391;
      GuiDraw.drawRect(x, this.area.y, x + width - 1, this.area.ey(), background);
      if (selected || draggedTab) {
         GuiDraw.drawRect(x, this.area.y, x + width - 1, this.area.y + 2, -10053172);
      }

      String label = this.getLabel(tab, width - CLOSE_WIDTH - 12);
      int color = selected || draggedTab ? -1 : -8355712;
      GuiDraw.drawString(this.font, label, x + 8, this.area.y + 7, color, false);
      int closeLeft = x + width - CLOSE_WIDTH;
      boolean closeHover = !draggingLayout && this.isCloseArea(context, x, width);
      this.drawCloseSymbol(context, closeLeft + CLOSE_WIDTH / 2, (this.area.y + this.area.ey()) / 2, closeHover ? -65536 : color);
   }

   public boolean mouseClicked(GuiContext context) {
      if (super.mouseClicked(context)) {
         return true;
      }
      if (!this.area.isInside(context)) {
         return false;
      }

      int index = this.getTabIndexAt(context.mouseX);
      if (index < 0) {
         return true;
      }

      String tab = (String)this.tabs.get(index);
      if (context.mouseButton == 2) {
         this.dragging = null;
         if (this.closeCallback != null) {
            this.closeCallback.accept(tab);
         }
         return true;
      }
      if (context.mouseButton != 0) {
         return false;
      }

      int left = this.getTabLeft(index);
      if (this.isCloseArea(context, left, this.getTabWidth(tab))) {
         this.dragging = null;
         if (this.closeCallback != null) {
            this.closeCallback.accept(tab);
         }
         return true;
      }

      this.dragging = tab;
      this.dragStartX = context.mouseX;
      this.dragMouseOffset = context.mouseX - left;
      return true;
   }

   public void mouseReleased(GuiContext context) {
      super.mouseReleased(context);
      if (this.dragging == null) {
         return;
      }

      String tab = this.dragging;
      boolean dragged = this.isVisualDrag(context);
      int from = this.tabs.indexOf(tab);
      int target = this.getDragTargetIndex(context);
      this.dragging = null;
      if (dragged) {
         if (from >= 0 && target >= 0 && target != from && this.reorderCallback != null) {
            this.reorderCallback.accept(tab, target);
         }

         return;
      }

      if (this.selectCallback != null && !tab.equals(this.active)) {
         this.selectCallback.accept(tab);
      }
   }

   private boolean isVisualDrag(GuiContext context) {
      return this.dragging != null && Math.abs(context.mouseX - this.dragStartX) > DRAG_THRESHOLD;
   }

   private int getDraggedTabLeft(GuiContext context) {
      int width = this.getTabWidth(this.dragging);
      int min = this.area.x;
      int max = Math.max(min, this.area.ex() - width);
      return Math.max(min, Math.min(max, context.mouseX - this.dragMouseOffset));
   }

   
   private int getDragTargetIndex(GuiContext context) {
      if (this.dragging == null) {
         return -1;
      }

      int center = this.getDraggedTabLeft(context) + this.getTabWidth(this.dragging) / 2;
      int x = this.area.x;
      int target = 0;

      for (String tab : this.tabs) {
         if (tab.equals(this.dragging)) {
            continue;
         }

         int width = this.getTabWidth(tab);
         if (center < x + width / 2) {
            break;
         }

         x += width;
         target++;
      }

      return target;
   }

   private boolean isCloseArea(GuiContext context, int left, int width) {
      int closeLeft = left + width - CLOSE_WIDTH;
      return context.mouseX >= closeLeft && context.mouseX < closeLeft + CLOSE_WIDTH
         && context.mouseY >= this.area.y && context.mouseY < this.area.ey();
   }

   private void drawCloseSymbol(GuiContext context, int centerX, int centerY, int color) {
      int glyphWidth = this.font.method_1727("×");
      int x = centerX - glyphWidth / 2 + 1;
      int y = centerY - 4;
      GuiDraw.drawString(this.font, "×", x, y, color, false);
   }

   private int getTabIndexAt(int mouseX) {
      int x = this.area.x;
      for (int index = 0; index < this.tabs.size(); ++index) {
         int width = this.getTabWidth((String)this.tabs.get(index));
         if (mouseX >= x && mouseX < x + width) {
            return index;
         }
         x += width;
      }
      return -1;
   }

   private int getTabLeft(int index) {
      int x = this.area.x;
      for (int current = 0; current < index; ++current) {
         x += this.getTabWidth((String)this.tabs.get(current));
      }
      return x;
   }

   private int getTabWidth(String tab) {
      return Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, this.font.method_1727(this.getDisplayName(tab)) + CLOSE_WIDTH + 18));
   }

   public int getTabWidthForExternal(String tab) {
      return this.getTabWidth(tab);
   }

   private String getLabel(String tab, int maxWidth) {
      String original = this.getDisplayName(tab);
      String label = original;
      while (!label.isEmpty() && this.font.method_1727(label) > maxWidth) {
         label = label.substring(0, label.length() - 1);
      }
      return label.length() < original.length() ? label + "…" : label;
   }

   private String getDisplayName(String tab) {
      int slash = Math.max(tab.lastIndexOf('/'), tab.lastIndexOf('\\'));
      return slash < 0 ? tab : tab.substring(slash + 1);
   }

   public static int getHeight() {
      return HEIGHT;
   }
}
