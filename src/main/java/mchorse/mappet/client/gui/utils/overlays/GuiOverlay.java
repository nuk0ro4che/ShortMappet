package mchorse.mappet.client.gui.utils.overlays;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.GuiUtils;
import net.minecraft.class_310;

public class GuiOverlay extends GuiElement {
   private boolean dimBackground = true;

   public static void addOverlay(GuiContext context, GuiOverlayPanel panel) {
      addOverlay(context, new GuiOverlay(context.mc, panel));
   }

   public static void addOverlay(GuiContext context, GuiOverlayPanel panel, float w, float h) {
      addOverlay(context, new GuiOverlay(context.mc, panel, w, h));
   }

   public static void addOverlay(GuiContext context, GuiOverlayPanel panel, int w, int h) {
      addOverlay(context, new GuiOverlay(context.mc, panel, w, h));
   }

   public static void addOverlay(GuiContext context, GuiOverlay overlay) {
      overlay.flex().relative(context.screen.root).wh(1.0F, 1.0F);
      context.screen.root.add(overlay);
      context.screen.root.resize();
   }

   public GuiOverlay(class_310 mc, GuiOverlayPanel overlay) {
      super(mc);
      overlay.flex().relative(this).xy(0.5F, 0.5F).wh(0.5F, 0.5F).anchor(0.5F, 0.5F);
      this.markContainer().add(overlay);
   }

   public GuiOverlay(class_310 mc, GuiOverlayPanel overlay, float w, float h) {
      this(mc, overlay);
      overlay.flex().wh(w, h);
   }

   public GuiOverlay(class_310 mc, GuiOverlayPanel overlay, int w, int h) {
      this(mc, overlay);
      overlay.flex().wh(w, h);
   }

   public GuiOverlay(class_310 mc, GuiOverlayPanel overlay, int w, int h, boolean dimBackground) {
      this(mc, overlay, w, h);
      this.dimBackground = dimBackground;
   }

   public static void addCard(GuiContext context, GuiOverlayPanel panel, GuiElement anchor, int width, int height) {
      GuiOverlay overlay = new GuiOverlay(context.mc, panel, width, height, false);
      overlay.flex().relative(context.screen.root).wh(1.0F, 1.0F);
      context.screen.root.add(overlay);
      context.screen.root.resize();

      int x = Math.max(4, anchor.area.x - width - 6);
      int y = Math.max(4, anchor.area.y);
      panel.flex().reset().relative(context.screen.root).xy(x, y).wh(width, height);
      context.screen.root.resize();
   }

   public void closeItself() {
      this.removeFromParent();
      GuiUtils.playClick();

      for(IGuiElement element : this.getChildren()) {
         if (element instanceof GuiOverlayPanel) {
            ((GuiOverlayPanel)element).onClose();
         }
      }

   }

   public boolean mouseClicked(GuiContext context) {
      if (super.mouseClicked(context)) {
         return true;
      }

      for (IGuiElement element : this.getChildren()) {
         if (element instanceof GuiOverlayPanel && !((GuiOverlayPanel)element).shouldCloseOnOutsideClick()) {
            return false;
         }
      }

      this.closeItself();
      return true;
   }

   public boolean mouseScrolled(GuiContext context) {
      super.mouseScrolled(context);
      return true;
   }

   public boolean keyTyped(GuiContext context) {
      if (context.keyCode == 1) {
         this.closeItself();
         return true;
      } else {
         return super.keyTyped(context);
      }
   }

   public void draw(GuiContext context) {
      if (this.dimBackground) {
         this.area.draw(-2013265920);
      }
      super.draw(context);
   }
}
