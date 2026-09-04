package mchorse.mappet.client.gui.ui;

import mchorse.mappet.client.gui.utils.GuiMorphRenderer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_310;





public class GuiUIMorphTooltipPreview extends GuiElement {
   private final GuiMorphRenderer renderer;
   private boolean hasMorph;
   private int visualX;
   private int visualY;
   private int visualWidth;
   private int visualHeight;
   private boolean drawnInCurrentFrame;
   private int tooltipWidth = 150;
   private int tooltipHeight = 142;

   public GuiUIMorphTooltipPreview(class_310 mc) {
      super(mc);
      this.renderer = new GuiMorphRenderer(mc);
      
      this.renderer.sandboxTreePass(true);
      this.renderer.flex().relative(this).wh(1.0F, 1.0F);
      this.add(this.renderer);
   }

   public void setMorph(AbstractMorph morph) {
      this.setMorph(morph, 150, 142, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public void setMorph(AbstractMorph morph, int width, int height, float scale, float yaw, float pitch, float offsetX, float offsetY, float offsetZ) {
      this.hasMorph = morph != null;
      this.tooltipWidth = Math.max(32, width);
      this.tooltipHeight = Math.max(48, height);
      this.renderer.morph.set(morph == null ? null : MorphUtils.copy(morph));
      this.renderer.scale = Math.max(0.05F, scale);
      

      this.renderer.fov = 45.0F;
      this.renderer.setRotation(yaw, pitch);
      this.renderer.setPosition(offsetX, offsetY, offsetZ);
   }

   private int previewWidth() {
      return Math.max(1, Math.min(this.area.w, this.tooltipWidth));
   }

   private int previewHeight() {
      return Math.max(1, Math.min(this.area.h, this.tooltipHeight));
   }

   private int previewX() {
      return this.area.mx() - this.previewWidth() / 2;
   }

   public void draw(GuiContext context) {
      this.drawnInCurrentFrame = true;
      this.area.draw(0xAA202024);
      int width = this.previewWidth();
      int height = this.previewHeight();
      int x = this.previewX();
      int y = this.area.y;
      

      this.visualX = Math.round((float)x + context.drawContext.method_51448().method_23760().method_23761().m30());
      this.visualY = Math.round((float)y + context.drawContext.method_51448().method_23760().method_23761().m31());
      this.visualWidth = width;
      this.visualHeight = height;
      GuiDraw.drawRect(x, y, x + width, y + height, 0xFF101014);
      GuiDraw.drawOutline(x, y, x + width, y + height, 0xFF53535B);

      if (!this.hasMorph) {
         String message = "Нет выбранного морфа";
         GuiDraw.drawStringWithShadow(this.font, message, x + width / 2 - this.font.method_1727(message) / 2, y + height / 2 - 4, 0xFFB8B8BE);
      }

      super.draw(context);
   }

   public boolean hasMorph() {
      return this.hasMorph;
   }

   
   public void beginFrame() {
      this.drawnInCurrentFrame = false;
   }

   public boolean wasDrawnInCurrentFrame() {
      return this.drawnInCurrentFrame;
   }

   




   private boolean isVisualHover(GuiContext context) {
      int width = this.visualWidth > 0 ? this.visualWidth : this.previewWidth();
      int height = this.visualHeight > 0 ? this.visualHeight : this.previewHeight();
      int x = this.visualWidth > 0 ? this.visualX : this.previewX();
      int y = this.visualHeight > 0 ? this.visualY : this.area.y;
      return context.mouseX >= x && context.mouseX < x + width && context.mouseY >= y && context.mouseY < y + height;
   }

   
   public boolean mouseScrolled(GuiContext context) {
      return false;
   }

   public boolean mouseClicked(GuiContext context) {
      return false;
   }


   
   public void drawPhysical(GuiContext context) {
      if (!this.hasMorph) {
         return;
      }

      int width = this.visualWidth > 0 ? this.visualWidth : this.previewWidth();
      int height = this.visualHeight > 0 ? this.visualHeight : this.previewHeight();
      int x = this.visualWidth > 0 ? this.visualX : this.previewX();
      int y = this.visualHeight > 0 ? this.visualY : this.area.y;
      
      this.renderer.area.set(x + 4, y + 4, Math.max(1, width - 8), Math.max(1, height - 28));
      this.renderer.sandboxTreePass(false);
      this.renderer.draw(context);
      this.renderer.sandboxTreePass(true);
   }
}
