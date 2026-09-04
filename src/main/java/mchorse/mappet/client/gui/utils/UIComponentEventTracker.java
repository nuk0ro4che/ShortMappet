package mchorse.mappet.client.gui.utils;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.components.UIComponent;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.class_310;


public class UIComponentEventTracker extends GuiElement {
   private final UIComponent component;
   private final UIContext uiContext;
   private boolean wasHovered;
   private long lastHoverEvent;

   public UIComponentEventTracker(class_310 mc, UIComponent component, UIContext uiContext) {
      super(mc);
      this.component = component;
      this.uiContext = uiContext;
   }

   public void draw(GuiContext context) {
      if (this.uiContext.editorPreview) {
         return;
      }
      boolean hovered = this.getParent() != null && this.getParent().area.isInside(context);
      long now = System.currentTimeMillis();
      if (hovered && !this.component.hoverEvent.isEmpty() && now - this.lastHoverEvent >= 50L) {
         this.uiContext.sendComponentEvent("Hover", this.component.id, context.mouseX, context.mouseY);
         this.lastHoverEvent = now;
      }
      if (hovered && !this.wasHovered && !this.component.hoverEnterEvent.isEmpty()) {
         this.uiContext.sendComponentEvent("HoverEnter", this.component.id, context.mouseX, context.mouseY);
      } else if (!hovered && this.wasHovered && !this.component.hoverExitEvent.isEmpty()) {
         this.uiContext.sendComponentEvent("HoverExit", this.component.id, context.mouseX, context.mouseY);
      }
      this.wasHovered = hovered;
   }
}
