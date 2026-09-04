package mchorse.mappet.client.gui.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.client.gui.utils.AnimatedUIComponentElement;
import mchorse.mappet.client.gui.utils.GuiMorphRenderer;
import mchorse.mappet.client.gui.utils.UIMorphTooltip;
import mchorse.mappet.api.ui.UIFile;
import mchorse.mclib.McLib;
import mchorse.mappet.api.ui.components.UIButtonComponent;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mappet.api.ui.components.UIGraphicsComponent;
import mchorse.mappet.api.ui.components.UIIconComponent;
import mchorse.mappet.api.ui.components.UILabelBaseComponent;
import mchorse.mappet.api.ui.components.UILayoutComponent;
import mchorse.mappet.api.ui.components.UIMorphComponent;
import mchorse.mappet.api.ui.components.UIParentComponent;
import mchorse.mappet.api.ui.components.UIStringListComponent;
import mchorse.mappet.api.ui.components.UITextboxComponent;
import mchorse.mappet.api.ui.components.UITextareaComponent;
import mchorse.mappet.api.ui.components.UITextComponent;
import mchorse.mappet.api.ui.components.UIToggleComponent;
import mchorse.mappet.api.ui.components.UITrackpadComponent;
import mchorse.mappet.api.ui.utils.UIUnit;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.tooltips.LabelTooltip;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiCanvas;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mappet.client.gui.panels.GuiUIFilePanel;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_310;
import net.minecraft.class_4587;






public class GuiUIEditorCanvas extends GuiCanvas {
   private static final int DESIGN_WIDTH = 1280;
   private static final int DESIGN_HEIGHT = 720;
   private static final int TOOLBAR_HEIGHT = 25;

   private enum Tool {
      SELECT,
      MOVE,
      RESIZE,
      ANCHOR
   }

   private enum ResizeHandle {
      NONE,
      TOP_LEFT,
      TOP,
      TOP_RIGHT,
      RIGHT,
      BOTTOM_RIGHT,
      BOTTOM,
      BOTTOM_LEFT,
      LEFT
   }
   private final Consumer<UIComponent> selection;
   private final Runnable changed;
   private final Function<UIComponent, GuiSimpleContextMenu> componentContext;
   private UIFile data;
   private UIComponent selected;
   private UIContext runtimeContext;
   private GuiElement runtimeHost;
   private boolean draggingComponent;
   private boolean panningSandbox;
   private Tool tool = Tool.MOVE;
   private Tool dragTool;
   private ResizeHandle resizeHandle = ResizeHandle.NONE;
   private int lastComponentX;
   private int lastComponentY;
   private int dragStartComponentX;
   private int dragStartComponentY;
   private int dragStartMouseX;
   private int dragStartMouseY;
   private int dragStartWidth;
   private int dragStartHeight;
   private int lastPanX;
   private int lastPanY;
   private double sandboxZoom = 1.0D;
   private int sandboxPanX;
   private int sandboxPanY;
   private boolean isDragging = false;
   private int gridSize = 10;

   private static class StringListArea {
      private final GuiStringListElement element;
      private final boolean visible;
      private final int x;
      private final int y;
      private final int w;
      private final int h;
      private final int scrollX;
      private final int scrollY;
      private final int scrollW;
      private final int scrollH;
      private final int scrollItemSize;
      private final int scrollSize;
      private final int scrollOffset;

      private StringListArea(GuiStringListElement element) {
         this.element = element;
         this.visible = element.isVisible();
         this.x = element.area.x;
         this.y = element.area.y;
         this.w = element.area.w;
         this.h = element.area.h;
         this.scrollX = element.scroll.x;
         this.scrollY = element.scroll.y;
         this.scrollW = element.scroll.w;
         this.scrollH = element.scroll.h;
         this.scrollItemSize = element.scroll.scrollItemSize;
         this.scrollSize = element.scroll.scrollSize;
         this.scrollOffset = element.scroll.scroll;
      }

      private void hideForTreePass() {
         this.element.setVisible(false);
      }

      private void restoreVisibility() {
         this.element.setVisible(this.visible);
      }

      private void project(Area target) {
         float scaleX = (float)target.w / (float)Math.max(1, this.w);
         float scaleY = (float)target.h / (float)Math.max(1, this.h);
         this.element.area.set(target.x, target.y, target.w, target.h);
         this.element.scroll.set(target.x, target.y, target.w, target.h);
         this.element.scroll.scrollItemSize = Math.max(1, Math.round((float)this.scrollItemSize * scaleY));
         this.element.scroll.setSize(Math.max(target.h, this.element.getList().size() * this.element.scroll.scrollItemSize));
         this.element.scroll.scroll = Math.max(0, Math.round((float)this.scrollOffset * scaleY));
      }

      private void restoreArea() {
         this.element.area.set(this.x, this.y, this.w, this.h);
         this.element.scroll.set(this.scrollX, this.scrollY, this.scrollW, this.scrollH);
         this.element.scroll.scrollItemSize = this.scrollItemSize;
         this.element.scroll.scrollSize = this.scrollSize;
         this.element.scroll.scroll = this.scrollOffset;
      }
   }

   private static class MorphRendererArea {
      private final GuiMorphRenderer renderer;
      private final int x;
      private final int y;
      private final int w;
      private final int h;

      private MorphRendererArea(GuiMorphRenderer renderer) {
         this.renderer = renderer;
         this.x = renderer.area.x;
         this.y = renderer.area.y;
         this.w = renderer.area.w;
         this.h = renderer.area.h;
      }

      private void restore() {
         this.renderer.area.set(this.x, this.y, this.w, this.h);
      }
   }

   public GuiUIEditorCanvas(class_310 mc, Consumer<UIComponent> selection, Runnable changed, Function<UIComponent, GuiSimpleContextMenu> componentContext) {
      super(mc);
      this.selection = selection;
      this.changed = changed;
      this.componentContext = componentContext;
      this.context(() -> this.componentContext == null || this.selected == null ? null : this.componentContext.apply(this.selected));
   }

   public void set(UIFile data, UIComponent selected) {
      boolean changedFile = this.data != data;
      this.data = data;
      this.selected = selected;
      if (changedFile) {
         this.draggingComponent = false;
         this.panningSandbox = false;
      }
      this.rebuildRuntimePreview();
   }

   




   private void rebuildRuntimePreview() {
      this.runtimeContext = null;
      this.runtimeHost = null;
      if (this.data == null) {
         return;
      }

      int runtimeWidth = this.getRuntimeWidth();
      int runtimeHeight = this.getRuntimeHeight();
      this.runtimeContext = new UIContext(this.data).editorPreview();
      this.runtimeHost = new GuiElement(this.mc).markContainer();
      this.runtimeHost.area.setPoints(0, 0, runtimeWidth, runtimeHeight);
      GuiElement root = this.data.root.create(this.mc, this.runtimeContext);
      root.flex().relative(this.runtimeHost).wh(1.0F, 1.0F);
      this.runtimeHost.add(root);
      this.runtimeHost.resize();
   }

   public void setSelected(UIComponent selected) {
      this.selected = selected;
   }

   public String getViewStatus() {
      String toolName = this.tool == Tool.SELECT ? "выбор" : this.tool == Tool.MOVE ? "перемещение" : this.tool == Tool.RESIZE ? "размер" : "точка поворота";
      return Math.round(this.sandboxZoom * 100.0D) + "%  |  Инструмент: " + toolName + "  |  ПКМ: панорама  |  Колесо: масштаб";
   }

   public boolean keyTyped(GuiContext context) {
      

      if (context.keyCode == 47) {
         this.selectTool(Tool.MOVE);
         return true;
      }
      if (context.keyCode == 31) {
         this.selectTool(Tool.RESIZE);
         return true;
      }
      if (context.keyCode == 25) {
         this.selectTool(Tool.ANCHOR);
         return true;
      }
      
      if (context.keyCode == 34) {
         this.gridSize = this.gridSize > 1 ? 1 : 10;
         return true;
      }
      
      if (context.keyCode == 61) {
         this.gridSize = Math.min(64, this.gridSize == 1 ? 2 : this.gridSize * 2);
         return true;
      }
      
      if (context.keyCode == 45) {
         this.gridSize = Math.max(1, this.gridSize > 2 ? this.gridSize / 2 : 1);
         return true;
      }

      return super.keyTyped(context);
   }

   private void selectTool(Tool tool) {
      this.tool = tool;
      this.draggingComponent = false;
      this.dragTool = null;
      this.resizeHandle = ResizeHandle.NONE;
   }

   private int snapToGrid(int value) {
      return Math.round((float)value / (float)this.gridSize) * this.gridSize;
   }

   private boolean isShiftDown(GuiContext context) {
      return context.keyCode == 42 || context.keyCode == 54;
   }

   public boolean mouseClicked(GuiContext context) {
      if (context.mouseButton == 0) {
         Tool clickedTool = this.getToolbarTool(context.mouseX, context.mouseY);
         if (clickedTool != null) {
            this.selectTool(clickedTool);
            return true;
         }
      }

      

      if (this.data != null && this.area.isInside(context) && context.mouseButton == 1) {
         UIComponent component = this.getTopComponent(context);
         if (component != null) {
            this.selected = component;
            this.draggingComponent = false;
            this.panningSandbox = false;
            this.selection.accept(component);
            

            return super.mouseClicked(context);
         }
      }

      
      boolean canvasClicked = super.mouseClicked(context);
      if (this.area.isInside(context) && (context.mouseButton == 1 || context.mouseButton == 2)) {
         this.panningSandbox = true;
         this.lastPanX = context.mouseX;
         this.lastPanY = context.mouseY;
         return true;
      }

      
      if (this.data != null && this.area.isInside(context) && context.mouseButton == 0) {
         UIComponent component = this.getTopComponent(context);
         if (component != null) {
            this.selected = component;
            this.draggingComponent = true;
            this.lastComponentX = context.mouseX;
            this.lastComponentY = context.mouseY;
            this.dragStartMouseX = context.mouseX;
            this.dragStartMouseY = context.mouseY;
            this.dragStartComponentX = this.getRuntimeComponentPosition(component, true);
            this.dragStartComponentY = this.getRuntimeComponentPosition(component, false);
            this.dragStartWidth = Math.max(1, (int)Math.round(this.resolve(component.w, this.getRuntimeWidth())));
            this.dragStartHeight = Math.max(1, (int)Math.round(this.resolve(component.h, this.getRuntimeHeight())));
            this.dragTool = this.tool;
            this.resizeHandle = this.dragTool == Tool.RESIZE ? this.getResizeHandle(this.getDataArea(component), context.mouseX, context.mouseY) : ResizeHandle.NONE;
            this.draggingComponent = this.dragTool != Tool.SELECT && (this.dragTool != Tool.RESIZE || this.resizeHandle != ResizeHandle.NONE);
            this.selection.accept(component);
            this.isDragging = true;
            return true;
         }

         this.draggingComponent = false;
         this.selected = null;
         this.selection.accept(null);
         return true;
      }

      return canvasClicked;
   }

   protected void dragging(GuiContext context) {
      if (this.panningSandbox) {
         this.sandboxPanX += context.mouseX - this.lastPanX;
         this.sandboxPanY += context.mouseY - this.lastPanY;
         this.lastPanX = context.mouseX;
         this.lastPanY = context.mouseY;
      } else if (this.draggingComponent && this.selected != null) {
         int x = context.mouseX;
         int y = context.mouseY;
         if (x != this.lastComponentX || y != this.lastComponentY) {
            Area sandbox = this.getSandboxArea();
            int uiDx = (int)Math.round((double)(x - this.dragStartMouseX) * (double)this.getRuntimeWidth() / (double)Math.max(1, sandbox.w));
            int uiDy = (int)Math.round((double)(y - this.dragStartMouseY) * (double)this.getRuntimeHeight() / (double)Math.max(1, sandbox.h));
            boolean snap = !this.isShiftDown(context) && this.gridSize > 1;
            if (this.dragTool == Tool.MOVE) {
               int px = this.dragStartComponentX + uiDx;
               int py = this.dragStartComponentY + uiDy;
               if (snap) {
                  int target = this.snapToAlignment(this.selected, px, py, this.dragStartWidth, this.dragStartHeight);
                  px = target >> 16;
                  py = target & 0xffff;
               }
               this.setComponentBounds(this.selected, px, py, this.dragStartWidth, this.dragStartHeight);
            } else if (this.dragTool == Tool.RESIZE) {
               this.resizeSelected(uiDx, uiDy);
               if (snap) {
                  this.snapSelectedToGrid();
               }
            } else if (this.dragTool == Tool.ANCHOR) {
               Area area = this.getDataArea(this.selected);
               float anchorX = this.clamp((float)(x - area.x) / (float)Math.max(1, area.w));
               float anchorY = this.clamp((float)(y - area.y) / (float)Math.max(1, area.h));
               this.selected.anchorX(anchorX);
               this.selected.anchorY(anchorY);
               this.setComponentBounds(this.selected, this.dragStartComponentX, this.dragStartComponentY, this.dragStartWidth, this.dragStartHeight);
            }
            this.syncRuntimeBounds(this.selected);
            this.lastComponentX = x;
            this.lastComponentY = y;
            this.changed.run();
         }
      } else {
         super.dragging(context);
      }
   }

   public void mouseReleased(GuiContext context) {
      super.mouseReleased(context);
      this.draggingComponent = false;
      this.dragTool = null;
      this.resizeHandle = ResizeHandle.NONE;
      this.panningSandbox = false;
      this.isDragging = false;
      
      if (this.selected != null) {
         GuiUIFilePanel panel = this.findParentPanel();
         if (panel != null) {
            panel.saveSnapshot();
         }
      }
   }

   public boolean mouseScrolled(GuiContext context) {
      if (this.area.isInside(context) && context.mouseWheel != 0) {
         Area before = this.getSandboxArea();
         double relativeX = ((double)context.mouseX - (double)before.x) / (double)Math.max(1, before.w);
         double relativeY = ((double)context.mouseY - (double)before.y) / (double)Math.max(1, before.h);
         this.sandboxZoom = Math.max(0.25D, Math.min(3.0D, this.sandboxZoom * (context.mouseWheel < 0 ? 1.15D : 1.0D / 1.15D)));
         Area centered = this.getSandboxArea(false);
         this.sandboxPanX = (int)Math.round((double)context.mouseX - relativeX * (double)centered.w - (double)centered.x);
         this.sandboxPanY = (int)Math.round((double)context.mouseY - relativeY * (double)centered.h - (double)centered.y);
         return true;
      }

      return super.mouseScrolled(context);
   }

   protected void drawCanvas(GuiContext context) {
      super.drawCanvas(context);
      this.drawCheckerBackground();
      this.drawDesignSurface();
      if (this.data == null) {
         return;
      }

      Area sandbox = this.getSandboxArea();
      if (this.runtimeHost == null) {
         GuiDraw.scissor(sandbox.x, sandbox.y, sandbox.w, sandbox.h, context);
         for(UIComponent component : this.getComponents()) {
            this.drawComponentPreview(component, this.getArea(component));
         }
         GuiDraw.unscissor(context);
      } else {
         this.drawRuntimePreview(context, sandbox);
      }
   }

   public void draw(GuiContext context) {
      super.draw(context);
      this.drawToolbar(context);
      if (this.selected != null) {
         Area sandbox = this.getSandboxArea();
         Area area = this.getArea(this.selected);
         int primary = -16777216 | (Integer)McLib.primaryColor.get() & 16777215;
         GuiDraw.scissor(sandbox.x, sandbox.y, sandbox.w, sandbox.h, context);
         GuiDraw.drawRect(area.x - 2, area.y - 2, area.ex() + 2, area.y, primary);
         GuiDraw.drawRect(area.x - 2, area.ey(), area.ex() + 2, area.ey() + 2, primary);
         GuiDraw.drawRect(area.x - 2, area.y, area.x, area.ey(), primary);
         GuiDraw.drawRect(area.ex(), area.y, area.ex() + 2, area.ey(), primary);
         if (this.tool == Tool.RESIZE) {
            this.drawResizeHandles(area, primary);
         } else if (this.tool == Tool.ANCHOR) {
            int pivotX = area.x + Math.round((float)area.w * this.selected.x.anchor);
            int pivotY = area.y + Math.round((float)area.h * this.selected.y.anchor);
            GuiDraw.drawRect(pivotX - 5, pivotY - 1, pivotX + 6, pivotY + 2, primary);
            GuiDraw.drawRect(pivotX - 1, pivotY - 5, pivotX + 2, pivotY + 6, primary);
         }
         GuiDraw.unscissor(context);
      }

      

      this.drawComponentTooltip(context);
      this.drawViewStatus(context);
   }

   private void drawComponentTooltip(GuiContext context) {
      if (this.data == null || !this.getSandboxArea().isInside(context)) {
         return;
      }

      


      context.resetTooltip();
      UIComponent component = this.getTopComponent(context);
      if (component == null) {
         return;
      }
      if (component.tooltipMorphEnabled && component.tooltipMorph != null) {
         this.drawMorphTooltip(context, component);
         return;
      }
      if (component.tooltip == null || component.tooltip.trim().isEmpty()) {
         return;
      }

      Direction direction = Direction.BOTTOM;
      if (component.tooltipDirection == 1) {
         direction = Direction.TOP;
      } else if (component.tooltipDirection == 2) {
         direction = Direction.RIGHT;
      } else if (component.tooltipDirection == 3) {
         direction = Direction.LEFT;
      }

      GuiElement element = this.runtimeContext == null || component.id.isEmpty() ? null : this.runtimeContext.getElement(component.id);
      if (element == null) {
         element = new GuiElement(this.mc);
      }
      Area original = new Area(element.area.x, element.area.y, element.area.w, element.area.h);
      Area source = this.getArea(component);
      element.area.set(source.x, source.y, source.w, source.h);
      context.tooltip.set(context, element);
      context.tooltip.draw(new LabelTooltip(IKey.str(component.tooltip.trim()), direction), context);
      context.resetTooltip();
      element.area.set(original.x, original.y, original.w, original.h);
   }

   
   private void drawPinnedTextTooltip(GuiContext context, UIComponent component) {
      String message = component.tooltip.trim();
      Area source = this.getArea(component);
      Area sandbox = this.getSandboxArea();
      int width = this.font.method_1727(message) + 8;
      int height = 16;
      int x = source.mx() - width / 2;
      int y = source.y - height - 6;
      if (component.tooltipDirection == 1) {
         y = source.ey() + 6;
      } else if (component.tooltipDirection == 2) {
         x = source.ex() + 6;
         y = source.my() - height / 2;
      } else if (component.tooltipDirection == 3) {
         x = source.x - width - 6;
         y = source.my() - height / 2;
      }
      x = Math.max(sandbox.x + 2, Math.min(x, sandbox.ex() - width - 2));
      y = Math.max(sandbox.y + 2, Math.min(y, sandbox.ey() - height - 2));
      GuiDraw.drawRect(x, y, x + width, y + height, 0xE0101010);
      GuiDraw.drawOutline(x, y, x + width, y + height, 0xFF53535B);
      GuiDraw.drawStringWithShadow(this.font, message, x + 4, y + 4, 0xFFFFFF);
   }

   private void drawMorphTooltip(GuiContext context, UIComponent component) {
      AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(component.tooltipMorph);
      GuiElement element = this.runtimeContext == null || component.id.isEmpty() ? null : this.runtimeContext.getElement(component.id);
      if (morph == null) {
         return;
      }
      if (element == null) {
         element = new GuiElement(this.mc);
      }

      Area original = new Area(element.area.x, element.area.y, element.area.w, element.area.h);
      Area source = this.getArea(component);
      element.area.set(source.x, source.y, source.w, source.h);
      context.tooltip.element = element;
      new UIMorphTooltip(this.mc, morph, component.tooltipMorphText, component.tooltipMorphWidth, component.tooltipMorphHeight, component.tooltipMorphScale, component.tooltipMorphYaw, component.tooltipMorphPitch, component.tooltipMorphOffsetX, component.tooltipMorphOffsetY, component.tooltipMorphOffsetZ, component.tooltipDirection).drawTooltip(context);
      context.resetTooltip();
      element.area.set(original.x, original.y, original.w, original.h);
   }

   private void drawViewStatus(GuiContext context) {
      if (this.area.w <= 220 || this.area.h <= 28) {
         return;
      }

      GuiDraw.scissor(this.area.x, this.area.y, this.area.w, this.area.h, context);
      GuiDraw.drawStringWithShadow(this.font, this.getViewStatus(), this.area.x + 8, this.area.ey() - 21, 14737632);
      GuiDraw.unscissor(context);
   }

   private void drawCheckerBackground() {
      final int cell = 12;
      final int dark = 0x55333333;
      final int light = 0x55404040;
      for(int y = this.area.y; y < this.area.ey(); y += cell) {
         for(int x = this.area.x; x < this.area.ex(); x += cell) {
            boolean odd = ((x - this.area.x) / cell + (y - this.area.y) / cell & 1) == 1;
            GuiDraw.drawRect(x, y, Math.min(x + cell, this.area.ex()), Math.min(y + cell, this.area.ey()), odd ? dark : light);
         }
      }
      GuiDraw.drawOutline(this.area.x, this.area.y, this.area.ex(), this.area.ey(), -1275068416);
   }

   private void drawDesignSurface() {
      Area sandbox = this.getSandboxArea();
      GuiDraw.drawRect(sandbox.x, sandbox.y, sandbox.ex(), sandbox.ey(), 0x66000000);
      GuiDraw.drawOutline(sandbox.x, sandbox.y, sandbox.ex(), sandbox.ey(), -8355712);

      
      if (this.gridSize > 1) {
         int gridColor = 0x33FFFFFF;
         for (int gx = sandbox.x; gx < sandbox.ex(); gx += this.gridSize) {
            GuiDraw.drawRect(gx, sandbox.y, gx + 1, sandbox.ey(), gridColor);
         }
         for (int gy = sandbox.y; gy < sandbox.ey(); gy += this.gridSize) {
            GuiDraw.drawRect(sandbox.x, gy, sandbox.ex(), gy + 1, gridColor);
         }

         
         this.drawAlignmentGuides(sandbox);
      }

      if (sandbox.w > 140 && sandbox.h > 28) {
         GuiDraw.drawStringWithShadow(this.font, "UI • GUI " + this.getRuntimeWidth() + " × " + this.getRuntimeHeight(), sandbox.x + 8, sandbox.y + 7, 12632256);
      }
   }

   private void drawAlignmentGuides(Area sandbox) {
      if (this.data == null || this.selected == null) {
         return;
      }
      int guideColor = 0xAAFF0000;
      Area area = this.getArea(this.selected);

      
      if (area.x > sandbox.x + 5) {
         GuiDraw.drawRect(area.x, sandbox.y, area.x + 1, sandbox.ey(), guideColor);
      }
      if (area.ex() < sandbox.ex() - 5) {
         GuiDraw.drawRect(area.ex(), sandbox.y, area.ex() + 1, sandbox.ey(), guideColor);
      }

      
      if (area.y > sandbox.y + 5) {
         GuiDraw.drawRect(sandbox.x, area.y, sandbox.ex(), area.y + 1, guideColor);
      }
      if (area.ey() < sandbox.ey() - 5) {
         GuiDraw.drawRect(sandbox.x, area.ey(), sandbox.ex(), area.ey() + 1, guideColor);
      }
   }

   private void drawRuntimePreview(GuiContext context, Area sandbox) {
      class_4587 matrices = context.drawContext.method_51448();
      int runtimeWidth = this.getRuntimeWidth();
      int runtimeHeight = this.getRuntimeHeight();
      Area virtualViewport = new Area();
      virtualViewport.setPoints(0, 0, runtimeWidth, runtimeHeight);
      

      this.runtimeHost.resize();
      List<StringListArea> stringLists = this.mapRuntimeStringListsToSandbox();
      List<MorphRendererArea> morphAreas = this.mapMorphRenderersToSandbox(sandbox, runtimeWidth, runtimeHeight);
      for (MorphRendererArea morphArea : morphAreas) {
         morphArea.renderer.sandboxTreePass(true);
      }

      matrices.method_22903();
      try {
         matrices.method_22904((double)sandbox.x, (double)sandbox.y, 0.0D);
         matrices.method_22905((float)sandbox.w / (float)runtimeWidth, (float)sandbox.h / (float)runtimeHeight, 1.0F);
         GuiDraw.scissor(sandbox.x, sandbox.y, sandbox.w, sandbox.h, context);
         context.pushViewport(virtualViewport);
         this.runtimeHost.draw(context);
      } finally {
         context.popViewport();
         GuiDraw.unscissor(context);
         matrices.method_22909();
         for (MorphRendererArea morphArea : morphAreas) {
            morphArea.renderer.sandboxTreePass(false);
         }
         for (StringListArea stringList : stringLists) {
            stringList.restoreVisibility();
         }
      }

      



      this.drawSandboxStringLists(context, sandbox);
      for (StringListArea stringList : stringLists) {
         stringList.restoreArea();
      }

      


      GuiDraw.scissor(sandbox.x, sandbox.y, sandbox.w, sandbox.h, context);
      try {
         for (MorphRendererArea morphArea : morphAreas) {
            morphArea.renderer.draw(context);
         }
      } finally {
         GuiDraw.unscissor(context);
         for (MorphRendererArea morphArea : morphAreas) {
            morphArea.restore();
         }
      }
   }

   private List<StringListArea> mapRuntimeStringListsToSandbox() {
      List<StringListArea> lists = new ArrayList();
      if (this.runtimeHost == null) {
         return lists;
      }

      for (GuiStringListElement list : this.runtimeHost.getChildren(GuiStringListElement.class, new ArrayList<GuiStringListElement>(), true)) {
         StringListArea state = new StringListArea(list);
         lists.add(state);
         UIComponent component = this.getStringListComponent(list);
         if (component != null) {
            state.project(this.getDataArea(component));
         }
         state.hideForTreePass();
      }

      return lists;
   }

   private void drawSandboxStringLists(GuiContext context, Area sandbox) {
      int runtimeWidth = this.getRuntimeWidth();
      int runtimeHeight = this.getRuntimeHeight();
      class_4587 matrices = context.drawContext.method_51448();
      matrices.method_22903();
      try {
         matrices.method_22904((double)sandbox.x, (double)sandbox.y, 0.0D);
         matrices.method_22905((float)sandbox.w / (float)runtimeWidth, (float)sandbox.h / (float)runtimeHeight, 1.0F);
         GuiDraw.scissor(sandbox.x, sandbox.y, sandbox.w, sandbox.h, context);
         for (UIComponent component : this.getComponents()) {
            if (component instanceof UIStringListComponent && component.visible) {
               this.drawStringList((UIStringListComponent)component, this.getRuntimeDataArea(component));
            }
         }
      } finally {
         GuiDraw.unscissor(context);
         matrices.method_22909();
      }
   }

   private List<MorphRendererArea> mapMorphRenderersToSandbox(Area sandbox, int runtimeWidth, int runtimeHeight) {
      List<MorphRendererArea> areas = new ArrayList();
      if (this.runtimeHost == null) {
         return areas;
      }

      for (GuiMorphRenderer renderer : this.runtimeHost.getChildren(GuiMorphRenderer.class, new ArrayList<GuiMorphRenderer>(), true)) {
         areas.add(new MorphRendererArea(renderer));
         UIComponent component = this.getMorphComponent(renderer);
         if (component != null) {
            



            Area target = this.getDataArea(component);
            renderer.area.set(target.x, target.y, target.w, target.h);
         }
      }

      return areas;
   }

   private int getRuntimeWidth() {
      return Math.max(1, this.mc.method_22683().method_4486());
   }

   private int getRuntimeHeight() {
      return Math.max(1, this.mc.method_22683().method_4502());
   }

   private Area getSandboxArea() {
      return this.getSandboxArea(true);
   }

   private Area getSandboxArea(boolean withPan) {
      int availableWidth = Math.max(1, this.area.w - 28);
      int availableHeight = Math.max(1, this.area.h - TOOLBAR_HEIGHT - 28);
      double scale = Math.min((double)availableWidth / (double)DESIGN_WIDTH, (double)availableHeight / (double)DESIGN_HEIGHT) * this.sandboxZoom;
      int width = Math.max(1, (int)Math.round((double)DESIGN_WIDTH * scale));
      int height = Math.max(1, (int)Math.round((double)DESIGN_HEIGHT * scale));
      int x = this.area.mx() - width / 2 + (withPan ? this.sandboxPanX : 0);
      int y = this.area.y + TOOLBAR_HEIGHT + availableHeight / 2 - height / 2 + (withPan ? this.sandboxPanY : 0);
      Area sandbox = new Area();
      sandbox.setPoints(x, y, x + width, y + height);
      return sandbox;
   }

   private void snapSelectedToGrid() {
      if (this.selected == null || this.gridSize <= 1) {
         return;
      }
      if (this.dragTool == Tool.RESIZE) {
         
      }
      this.syncRuntimeBounds(this.selected);
   }

   private int snapToAlignment(UIComponent target, int x, int y, int w, int h) {
      int tolerance = 5;
      int bestX = this.snapToGrid(x);
      int bestY = this.snapToGrid(y);
      for (UIComponent sibling : this.getSiblings(target)) {
         Area area = this.getDataArea(sibling);
         if (Math.abs(area.x - bestX) <= tolerance) {
            bestX = this.snapToGrid(area.x);
         }
         if (Math.abs(area.y - bestY) <= tolerance) {
            bestY = this.snapToGrid(area.y);
         }
         if (Math.abs(area.ex() - (bestX + w)) <= tolerance) {
            bestX = this.snapToGrid(area.ex() - w);
         }
         if (Math.abs(area.ey() - (bestY + h)) <= tolerance) {
            bestY = this.snapToGrid(area.ey() - h);
         }
      }
      return (bestX << 16) | (bestY & 0xffff);
   }

   private List<UIComponent> getSiblings(UIComponent target) {
      List<UIComponent> siblings = new ArrayList<>();
      UIComponent parent = this.getParent(target);
      if (parent instanceof UIParentComponent) {
         for (UIComponent child : ((UIParentComponent)parent).children) {
            if (child != target) {
               siblings.add(child);
            }
         }
      }
      return siblings;
   }

   private UIComponent getParent(UIComponent target) {
      UIComponent root = this.data.root;
      if (root == target) {
         return null;
      }
      return this.findParent(root, target);
   }

   private UIComponent findParent(UIComponent current, UIComponent target) {
      if (current instanceof UIParentComponent) {
         for (UIComponent child : ((UIParentComponent)current).children) {
            if (child == target) {
               return current;
            }
            UIComponent found = this.findParent(child, target);
            if (found != null) {
               return found;
            }
         }
      }
      return null;
   }

   private void drawComponentPreview(UIComponent component, Area area) {
      if (!component.visible || area.w <= 0 || area.h <= 0) {
         return;
      }
      if (component instanceof UIButtonComponent) {
         this.drawButton((UILabelBaseComponent)component, area);
      } else if (component instanceof UITextboxComponent || component instanceof UITextareaComponent) {
         this.drawInput((UILabelBaseComponent)component, area, component instanceof UITextareaComponent);
      } else if (component instanceof UIToggleComponent) {
         this.drawToggle((UILabelBaseComponent)component, area);
      } else if (component instanceof UITrackpadComponent) {
         this.drawTrackpad(area);
      } else if (component instanceof UIStringListComponent) {
         this.drawStringList((UIStringListComponent)component, area);
      } else if (component instanceof UILabelBaseComponent) {
         this.drawLabel((UILabelBaseComponent)component, area, component instanceof UITextComponent);
      } else if (component instanceof UILayoutComponent) {
         this.drawLayout(component, area);
      } else if (component instanceof UIGraphicsComponent || component instanceof UIIconComponent) {
         this.drawGraphic(component, area);
      } else {
         GuiDraw.drawRect(area.x, area.y, area.ex(), area.ey(), 1006632960);
         GuiDraw.drawOutline(area.x, area.y, area.ex(), area.ey(), -12566464);
         this.drawCaption(this.componentName(component), area, 11184810);
      }
   }

   private void drawButton(UILabelBaseComponent component, Area area) {
      GuiDraw.drawRect(area.x, area.y, area.ex(), area.ey(), 0xFF06345F);
      GuiDraw.drawRect(area.x + 1, area.y + 1, area.ex() - 1, area.ey() - 1, 0xFF0A64B8);
      GuiDraw.drawRect(area.x + 2, area.y + 2, area.ex() - 2, Math.min(area.y + 4, area.ey() - 1), 0xFF338DD4);
      GuiDraw.drawOutline(area.x, area.y, area.ex(), area.ey(), 0xFF0C4D8A);
      this.drawCentered(this.labelOf(component, "Кнопка"), area, 16777215);
   }

   private void drawInput(UILabelBaseComponent component, Area area, boolean multiline) {
      GuiDraw.drawRect(area.x, area.y, area.ex(), area.ey(), -15724528);
      GuiDraw.drawOutline(area.x, area.y, area.ex(), area.ey(), -6710887);
      String value = this.labelOf(component, multiline ? "Текстовая область" : "Введите текст");
      GuiDraw.drawStringWithShadow(this.font, value, area.x + 4, area.y + Math.max(3, area.h / 2 - 4), 11184810);
      if (!multiline && area.w > 16) {
         GuiDraw.drawRect(Math.min(area.x + this.font.method_1727(value) + 6, area.ex() - 3), area.y + 4, Math.min(area.x + this.font.method_1727(value) + 7, area.ex() - 2), Math.max(area.y + 5, area.ey() - 4), -1);
      }
   }

   private void drawToggle(UILabelBaseComponent component, Area area) {
      int size = Math.max(8, Math.min(area.h - 4, 14));
      int x = area.x + 3;
      int y = area.my() - size / 2;
      GuiDraw.drawRect(x, y, x + size, y + size, -15724528);
      GuiDraw.drawOutline(x, y, x + size, y + size, -6710887);
      GuiDraw.drawStringWithShadow(this.font, this.labelOf(component, "Переключатель"), x + size + 5, area.my() - 4, 16777215);
   }

   private void drawTrackpad(Area area) {
      GuiDraw.drawRect(area.x, area.y, area.ex(), area.ey(), -15724528);
      GuiDraw.drawOutline(area.x, area.y, area.ex(), area.ey(), -6710887);
      int center = area.mx();
      GuiDraw.drawRect(center - 1, area.y + 2, center + 1, area.ey() - 2, -8421505);
      this.drawCentered("0", area, 16777215);
   }

   private void drawStringList(UIStringListComponent component, Area area) {
      if (component.background != null) {
         GuiDraw.drawRect(area.x, area.y, area.ex(), area.ey(), component.background);
      }
      int row = 16;
      int index = 0;
      for(String value : component.values) {
         int y = area.y + index * row;
         if (y + row > area.ey()) {
            break;
         }
         boolean selected = component.selected != null && component.selected == index;
         if (selected) {
            int primary = -2013265920 + (Integer)McLib.primaryColor.get();
            GuiDraw.drawRect(area.x, y, area.ex(), y + row, primary);
         }
         int textY = y + row / 2 - GuiDraw.fontHeight(this.font) / 2;
         GuiDraw.drawStringWithShadow(this.font, value, area.x + 4, textY, selected ? 16777120 : 16777215);
         ++index;
      }
   }

   private void drawLabel(UILabelBaseComponent component, Area area, boolean centered) {
      String label = this.labelOf(component, component instanceof UITextComponent ? "Текст" : "Надпись");
      if (centered) {
         this.drawCentered(label, area, 16777215);
      } else {
         GuiDraw.drawStringWithShadow(this.font, label, area.x + 2, area.my() - 4, 16777215);
      }
   }

   private void drawLayout(UIComponent component, Area area) {
      GuiDraw.drawRect(area.x, area.y, area.ex(), area.ey(), 50397183);
      GuiDraw.drawOutline(area.x, area.y, area.ex(), area.ey(), -10066330);
      GuiDraw.drawRect(area.x, area.y, area.ex(), Math.min(area.y + 12, area.ey()), -14540254);
      this.drawCaption(this.componentName(component), area, 13421772);
   }

   private void drawGraphic(UIComponent component, Area area) {
      GuiDraw.drawRect(area.x, area.y, area.ex(), area.ey(), -14671840);
      GuiDraw.drawOutline(area.x, area.y, area.ex(), area.ey(), -8421505);
      int cx = area.mx();
      int cy = area.my();
      GuiDraw.drawRect(cx - 5, cy - 5, cx + 6, cy + 6, -8420865);
      GuiDraw.drawRect(cx - 2, cy - 2, cx + 3, cy + 3, -1);
      this.drawCaption(this.componentName(component), area, 11184810);
   }

   private void drawCaption(String label, Area area, int color) {
      if (area.w > this.font.method_1727(label) + 6 && area.h > 12) {
         GuiDraw.drawStringWithShadow(this.font, label, area.x + 3, area.y + 3, color);
      }
   }

   private void drawCentered(String label, Area area, int color) {
      GuiDraw.drawStringWithShadow(this.font, label, area.mx() - this.font.method_1727(label) / 2, area.my() - 4, color);
   }

   private String labelOf(UILabelBaseComponent component, String fallback) {
      return component.label == null || component.label.isEmpty() ? fallback : component.label;
   }

   private String componentName(UIComponent component) {
      return component.id == null || component.id.isEmpty() ? component.getClass().getSimpleName().replace("UI", "") : component.id;
   }

   private void drawGrid() {
      int step = this.scaleX.getZoom() >= 0.8D ? 16 : 32;
      int minX = ((int)Math.floor(this.fromX(this.area.x) / step) - 1) * step;
      int maxX = ((int)Math.ceil(this.fromX(this.area.ex()) / step) + 1) * step;
      int minY = ((int)Math.floor(this.fromY(this.area.y) / step) - 1) * step;
      int maxY = ((int)Math.ceil(this.fromY(this.area.ey()) / step) + 1) * step;

      for(int x = minX; x <= maxX; x += step) {
         int sx = this.toX((double)x);
         GuiDraw.drawRect(sx, this.area.y, sx + 1, this.area.ey(), x % (step * 4) == 0 ? 671088640 : 335544320);
      }
      for(int y = minY; y <= maxY; y += step) {
         int sy = this.toY((double)y);
         GuiDraw.drawRect(this.area.x, sy, this.area.ex(), sy + 1, y % (step * 4) == 0 ? 671088640 : 335544320);
      }

      int left = this.toX(0.0D);
      int top = this.toY(0.0D);
      int right = this.toX((double)DESIGN_WIDTH);
      int bottom = this.toY((double)DESIGN_HEIGHT);
      GuiDraw.drawOutline(left, top, right, bottom, 1711276032);
   }

   private UIComponent getTopComponent(GuiContext context) {
      List<UIComponent> components = this.getComponents();
      Collections.reverse(components);
      for (UIComponent component : components) {
         if (this.getArea(component).isInside(context)) {
            return component;
         }
      }

      return null;
   }

   private List<UIComponent> getComponents() {
      List<UIComponent> components = new ArrayList();
      if (this.data != null) {
         this.collect(this.data.root, components);
      }
      return components;
   }

   private void collect(UIParentComponent parent, List<UIComponent> components) {
      for(UIComponent component : parent.children) {
         components.add(component);
         if (component instanceof UIParentComponent) {
            this.collect((UIParentComponent)component, components);
         }
      }
   }

   private Area getArea(UIComponent component) {
      return this.getDataArea(component);
   }

   private Area getDataArea(UIComponent component) {
      return this.projectRuntimeArea(this.getRuntimeDataArea(component));
   }

   private Area getRuntimeDataArea(UIComponent component) {
      int runtimeWidth = this.getRuntimeWidth();
      int runtimeHeight = this.getRuntimeHeight();
      double width = Math.max(1.0D, this.resolve(component.w, runtimeWidth));
      double height = Math.max(1.0D, this.resolve(component.h, runtimeHeight));
      double x = this.resolve(component.x, runtimeWidth) - width * (double)component.x.anchor;
      double y = this.resolve(component.y, runtimeHeight) - height * (double)component.y.anchor;
      Area runtimeArea = new Area();
      runtimeArea.setPoints((int)Math.round(x), (int)Math.round(y), (int)Math.round(x + width), (int)Math.round(y + height));
      return runtimeArea;
   }

   private ResizeHandle getResizeHandle(Area area, int x, int y) {
      int distance = 7;
      boolean left = Math.abs(x - area.x) <= distance;
      boolean right = Math.abs(x - area.ex()) <= distance;
      boolean top = Math.abs(y - area.y) <= distance;
      boolean bottom = Math.abs(y - area.ey()) <= distance;
      if (left && top) return ResizeHandle.TOP_LEFT;
      if (right && top) return ResizeHandle.TOP_RIGHT;
      if (right && bottom) return ResizeHandle.BOTTOM_RIGHT;
      if (left && bottom) return ResizeHandle.BOTTOM_LEFT;
      if (top && x >= area.x && x <= area.ex()) return ResizeHandle.TOP;
      if (right && y >= area.y && y <= area.ey()) return ResizeHandle.RIGHT;
      if (bottom && x >= area.x && x <= area.ex()) return ResizeHandle.BOTTOM;
      if (left && y >= area.y && y <= area.ey()) return ResizeHandle.LEFT;
      return ResizeHandle.NONE;
   }

   private void resizeSelected(int dx, int dy) {
      int left = this.dragStartComponentX;
      int top = this.dragStartComponentY;
      int right = left + this.dragStartWidth;
      int bottom = top + this.dragStartHeight;
      if (this.resizeHandle == ResizeHandle.TOP_LEFT || this.resizeHandle == ResizeHandle.LEFT || this.resizeHandle == ResizeHandle.BOTTOM_LEFT) left += dx;
      if (this.resizeHandle == ResizeHandle.TOP_LEFT || this.resizeHandle == ResizeHandle.TOP || this.resizeHandle == ResizeHandle.TOP_RIGHT) top += dy;
      if (this.resizeHandle == ResizeHandle.TOP_RIGHT || this.resizeHandle == ResizeHandle.RIGHT || this.resizeHandle == ResizeHandle.BOTTOM_RIGHT) right += dx;
      if (this.resizeHandle == ResizeHandle.BOTTOM_LEFT || this.resizeHandle == ResizeHandle.BOTTOM || this.resizeHandle == ResizeHandle.BOTTOM_RIGHT) bottom += dy;
      if (right <= left) {
         if (this.resizeHandle == ResizeHandle.TOP_LEFT || this.resizeHandle == ResizeHandle.LEFT || this.resizeHandle == ResizeHandle.BOTTOM_LEFT) left = right - 1; else right = left + 1;
      }
      if (bottom <= top) {
         if (this.resizeHandle == ResizeHandle.TOP_LEFT || this.resizeHandle == ResizeHandle.TOP || this.resizeHandle == ResizeHandle.TOP_RIGHT) top = bottom - 1; else bottom = top + 1;
      }
      this.setComponentBounds(this.selected, left, top, right - left, bottom - top);
   }

   private void setComponentBounds(UIComponent component, int left, int top, int width, int height) {
      int safeWidth = Math.max(1, width);
      int safeHeight = Math.max(1, height);
      if (!component.x.relative) {
         safeWidth = this.snapToGrid(safeWidth);
      }
      if (!component.y.relative) {
         safeHeight = this.snapToGrid(safeHeight);
      }
      component.w(safeWidth);
      component.h(safeHeight);
      component.x(left + Math.round((float)safeWidth * component.x.anchor));
      component.y(top + Math.round((float)safeHeight * component.y.anchor));
   }

   private void drawResizeHandles(Area area, int color) {
      this.drawResizeHandle(area.x, area.y, color);
      this.drawResizeHandle(area.mx(), area.y, color);
      this.drawResizeHandle(area.ex(), area.y, color);
      this.drawResizeHandle(area.ex(), area.my(), color);
      this.drawResizeHandle(area.ex(), area.ey(), color);
      this.drawResizeHandle(area.mx(), area.ey(), color);
      this.drawResizeHandle(area.x, area.ey(), color);
      this.drawResizeHandle(area.x, area.my(), color);
   }

   private void drawResizeHandle(int x, int y, int color) {
      GuiDraw.drawRect(x - 3, y - 3, x + 4, y + 4, color);
   }

   private void drawToolbar(GuiContext context) {
      int x = this.area.x + 6;
      int y = this.area.y + 4;
      GuiDraw.drawRect(x - 2, y - 2, x + 82, y + 20, 0xAA11131A);
      Tool[] tools = Tool.values();
      for (int i = 0; i < tools.length; ++i) {
         Tool current = tools[i];
         int bx = x + i * 20;
         int color = current == this.tool ? (-16777216 | (Integer)McLib.primaryColor.get() & 16777215) : 0x66303038;
         GuiDraw.drawRect(bx, y, bx + 18, y + 18, color);
      }

      
      int gridX = x + 84;
      int gridY = y + 2;
      GuiDraw.drawRect(gridX - 1, gridY - 1, gridX + 50, gridY + 11, 0xAA11131A);
      GuiDraw.drawOutline(gridX - 1, gridY - 1, gridX + 50, gridY + 11, -1);
      GuiDraw.drawStringWithShadow(this.font, String.valueOf(this.gridSize), gridX + 21, gridY + 2, -1);

      

      class_4587 matrices = context.drawContext.method_51448();
      matrices.method_22903();
      try {
         matrices.method_22904(0.5D, 0.5D, 0.0D);
         for (int i = 0; i < tools.length; ++i) {
            Tool current = tools[i];
            int bx = x + i * 20;
            this.drawToolGlyph(current, bx + 8, y + 8, current == this.tool ? -1 : -4144960);
         }
      } finally {
         matrices.method_22909();
      }
   }

   private void drawToolGlyph(Tool tool, int x, int y, int color) {
      if (tool == Tool.SELECT) {
         GuiDraw.drawOutline(x - 4, y - 4, x + 5, y + 5, color);
      } else if (tool == Tool.MOVE) {
         GuiDraw.drawRect(x - 5, y, x + 6, y + 1, color);
         GuiDraw.drawRect(x, y - 5, x + 1, y + 6, color);
         GuiDraw.drawRect(x - 5, y - 1, x - 3, y + 2, color);
         GuiDraw.drawRect(x + 4, y - 1, x + 6, y + 2, color);
         GuiDraw.drawRect(x - 1, y - 5, x + 2, y - 3, color);
         GuiDraw.drawRect(x - 1, y + 4, x + 2, y + 6, color);
      } else if (tool == Tool.RESIZE) {
         GuiDraw.drawOutline(x - 5, y - 5, x + 6, y + 6, color);
         GuiDraw.drawRect(x - 2, y + 2, x + 5, y + 3, color);
         GuiDraw.drawRect(x + 2, y - 2, x + 3, y + 5, color);
      } else {
         GuiDraw.drawRect(x - 5, y, x + 6, y + 1, color);
         GuiDraw.drawRect(x, y - 5, x + 1, y + 6, color);
         GuiDraw.drawRect(x - 1, y - 1, x + 2, y + 2, color);
      }
   }

   private Tool getToolbarTool(int x, int y) {
      int startX = this.area.x + 6;
      int startY = this.area.y + 4;
      if (y < startY || y >= startY + 18 || x < startX || x >= startX + 78) {
         return null;
      }
      int index = (x - startX) / 20;
      Tool[] tools = Tool.values();
      return index >= 0 && index < tools.length ? tools[index] : null;
   }

   private float clamp(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }

   private GuiUIFilePanel findParentPanel() {
      GuiElement parent = this.getParent();
      while (parent != null) {
         if (parent instanceof GuiUIFilePanel) {
            return (GuiUIFilePanel)parent;
         }
         parent = parent.getParent();
      }
      return null;
   }

   private void syncRuntimeBounds(UIComponent component) {
      if (this.runtimeContext == null || this.runtimeHost == null || component == null || component.id == null || component.id.isEmpty()) {
         return;
      }

      GuiElement element = this.runtimeContext.getElement(component.id);
      if (element == null) {
         return;
      }

      

      GuiElement layoutElement = element.getParent() instanceof AnimatedUIComponentElement ? element.getParent() : element;
      component.x.apply(layoutElement.flex().x, this.runtimeContext);
      component.y.apply(layoutElement.flex().y, this.runtimeContext);
      component.w.apply(layoutElement.flex().w, this.runtimeContext);
      component.h.apply(layoutElement.flex().h, this.runtimeContext);
      this.runtimeHost.resize();
   }

   private int getRuntimeComponentPosition(UIComponent component, boolean x) {
      int extent = x ? this.getRuntimeWidth() : this.getRuntimeHeight();
      UIUnit position = x ? component.x : component.y;
      UIUnit size = x ? component.w : component.h;
      return (int)Math.round(this.resolve(position, extent) - Math.max(1.0D, this.resolve(size, extent)) * (double)position.anchor);
   }

   private UIComponent getStringListComponent(GuiStringListElement list) {
      if (this.runtimeContext == null) {
         return null;
      }

      for (UIComponent component : this.getComponents()) {
         if (component instanceof UIStringListComponent && component.id != null && !component.id.isEmpty() && this.runtimeContext.getElement(component.id) == list) {
            return component;
         }
      }

      return null;
   }

   private UIComponent getMorphComponent(GuiMorphRenderer renderer) {
      if (this.runtimeContext == null) {
         return null;
      }

      for (UIComponent component : this.getComponents()) {
         if (component instanceof UIMorphComponent && component.id != null && !component.id.isEmpty() && this.runtimeContext.getElement(component.id) == renderer) {
            return component;
         }
      }

      return null;
   }

   private Area projectRuntimeArea(Area runtimeArea) {
      Area sandbox = this.getSandboxArea();
      int runtimeWidth = this.getRuntimeWidth();
      int runtimeHeight = this.getRuntimeHeight();
      double scaleX = (double)sandbox.w / (double)runtimeWidth;
      double scaleY = (double)sandbox.h / (double)runtimeHeight;
      Area area = new Area();
      area.setPoints(sandbox.x + (int)Math.round((double)runtimeArea.x * scaleX), sandbox.y + (int)Math.round((double)runtimeArea.y * scaleY), sandbox.x + (int)Math.round((double)(runtimeArea.x + runtimeArea.w) * scaleX), sandbox.y + (int)Math.round((double)(runtimeArea.y + runtimeArea.h) * scaleY));
      return area;
   }

   private double resolve(UIUnit unit, int size) {
      return (double)unit.offset + (double)unit.value * (double)size;
   }
}
