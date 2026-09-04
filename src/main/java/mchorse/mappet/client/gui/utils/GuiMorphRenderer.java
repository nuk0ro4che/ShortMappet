package mchorse.mappet.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.GuiModelRenderer;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.api.Morph;
import net.minecraft.class_310;

public class GuiMorphRenderer extends GuiModelRenderer {
   public Morph morph = new Morph();
   private boolean sandboxTreePass;

   public GuiMorphRenderer(class_310 mc) {
      super(mc);
   }

   





   public void sandboxTreePass(boolean sandboxTreePass) {
      this.sandboxTreePass = sandboxTreePass;
   }

   public void draw(GuiContext context) {
      if (!this.sandboxTreePass) {
         super.draw(context);
      }
   }

   protected void update() {
      super.update();
      if (!this.morph.isEmpty()) {
         this.morph.get().update(this.entity);
      }

   }

   protected void drawUserModel(GuiContext context) {
      if (this.entity == null || this.morph.isEmpty()) {
         return;
      }

      GuiModelRenderer.disableRenderingFlag();
      mchorse.metamorph.client.gui.creative.GuiMorphRenderer.renderInFrame(class_310.method_1551(), this.morph.get(), this.entity, 0.0D, 0.0D, 0.0D, this.entity.field_6283, context.partialTicks);
   }

   protected void drawGround() {
   }
}
