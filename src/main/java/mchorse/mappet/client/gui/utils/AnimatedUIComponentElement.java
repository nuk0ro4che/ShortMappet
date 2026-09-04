package mchorse.mappet.client.gui.utils;

import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.resizers.Flex.Measure;
import mchorse.mclib.utils.Interpolation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Quaternionf;






@Environment(EnvType.CLIENT)
public class AnimatedUIComponentElement extends GuiElement {
   private float moveTargetRelativeX;
   private float moveTargetRelativeY;
   private float moveFromScreenX;
   private float moveFromScreenY;
   private float moveTargetScreenX;
   private float moveTargetScreenY;
   private float moveOffsetX;
   private float moveOffsetY;
   private long moveStartedAt = -1L;
   private int moveDuration;
   private Interpolation moveInterpolation = Interpolation.SINE_INOUT;
   private boolean movePending;
   private float rotation;
   private float rotationFrom;
   private float rotationTarget;
   private long rotationStartedAt = -1L;
   private int rotationDuration;
   private Interpolation rotationInterpolation = Interpolation.SINE_INOUT;
   private boolean rotationPending;

   public AnimatedUIComponentElement(class_310 mc, GuiElement child) {
      super(mc);
      this.markContainer();
      this.add(child);
      child.flex().relative(this).wh(1.0F, 1.0F);
   }

   public void moveTo(float x, float y, int duration, Interpolation interpolation) {
      this.moveTargetRelativeX = x;
      this.moveTargetRelativeY = y;
      this.moveDuration = Math.max(0, duration);
      this.moveInterpolation = interpolation == null ? Interpolation.SINE_INOUT : interpolation;
      this.movePending = true;
   }

   public void rotateTo(float rotation, int duration, Interpolation interpolation) {
      this.rotationTarget = rotation;
      this.rotationDuration = Math.max(0, duration);
      this.rotationInterpolation = interpolation == null ? Interpolation.SINE_INOUT : interpolation;
      this.rotationPending = true;
   }

   public void draw(GuiContext context) {
      this.updateAnimations(context);
      if (this.moveOffsetX == 0.0F && this.moveOffsetY == 0.0F && this.rotation == 0.0F) {
         super.draw(context);
         return;
      }

      float centerX = (float)this.area.x + (float)this.area.w / 2.0F;
      float centerY = (float)this.area.y + (float)this.area.h / 2.0F;
      int mouseX = context.mouseX;
      int mouseY = context.mouseY;
      class_4587 matrices = context.drawContext.method_51448();
      matrices.method_22903();
      try {
         matrices.method_22904((double)this.moveOffsetX, (double)this.moveOffsetY, 0.0D);
         matrices.method_22904((double)centerX, (double)centerY, 0.0D);
         matrices.method_22907((new Quaternionf()).rotationAxis((float)Math.toRadians((double)this.rotation), 0.0F, 0.0F, 1.0F));
         matrices.method_22904((double)(-centerX), (double)(-centerY), 0.0D);
         this.remapPointer(context);
         super.draw(context);
      } finally {
         context.mouseX = mouseX;
         context.mouseY = mouseY;
         matrices.method_22909();
      }
   }

   public boolean mouseClicked(GuiContext context) {
      int mouseX = context.mouseX;
      int mouseY = context.mouseY;
      try {
         this.remapPointer(context);
         return super.mouseClicked(context);
      } finally {
         context.mouseX = mouseX;
         context.mouseY = mouseY;
      }
   }

   
   private void remapPointer(GuiContext context) {
      if (this.moveOffsetX == 0.0F && this.moveOffsetY == 0.0F && this.rotation == 0.0F) {
         return;
      }

      float centerX = (float)this.area.x + (float)this.area.w / 2.0F;
      float centerY = (float)this.area.y + (float)this.area.h / 2.0F;
      float dx = (float)context.mouseX - (centerX + this.moveOffsetX);
      float dy = (float)context.mouseY - (centerY + this.moveOffsetY);
      double radians = Math.toRadians((double)(-this.rotation));
      float cos = (float)Math.cos(radians);
      float sin = (float)Math.sin(radians);
      context.mouseX = Math.round(centerX + dx * cos - dy * sin);
      context.mouseY = Math.round(centerY + dx * sin + dy * cos);
   }

   private void updateAnimations(GuiContext context) {
      long now = System.nanoTime();
      if (this.movePending) {
         this.moveFromScreenX = (float)this.area.x + this.moveOffsetX;
         this.moveFromScreenY = (float)this.area.y + this.moveOffsetY;
         this.moveTargetScreenX = this.getTargetScreenX(this.moveTargetRelativeX);
         this.moveTargetScreenY = this.getTargetScreenY(this.moveTargetRelativeY);
         this.moveStartedAt = now;
         this.movePending = false;
      }
      if (this.rotationPending) {
         this.rotationFrom = this.rotation;
         this.rotationStartedAt = now;
         this.rotationPending = false;
      }

      if (this.moveStartedAt >= 0L) {
         float factor = this.getFactor(now, this.moveStartedAt, this.moveDuration);
         float currentX = this.moveInterpolation.interpolate(this.moveFromScreenX, this.moveTargetScreenX, factor);
         float currentY = this.moveInterpolation.interpolate(this.moveFromScreenY, this.moveTargetScreenY, factor);
         this.moveOffsetX = currentX - (float)this.area.x;
         this.moveOffsetY = currentY - (float)this.area.y;
         if (factor >= 1.0F) {
            this.flex().x.set(this.moveTargetRelativeX, Measure.RELATIVE);
            this.flex().y.set(this.moveTargetRelativeY, Measure.RELATIVE);
            this.resize();
            this.moveOffsetX = 0.0F;
            this.moveOffsetY = 0.0F;
            this.moveStartedAt = -1L;
         }
      }

      if (this.rotationStartedAt >= 0L) {
         float factor = this.getFactor(now, this.rotationStartedAt, this.rotationDuration);
         this.rotation = this.rotationInterpolation.interpolate(this.rotationFrom, this.rotationTarget, factor);
         if (factor >= 1.0F) this.rotationStartedAt = -1L;
      }
   }

   private float getTargetScreenX(float relative) {
      GuiElement parent = this.getParent();
      if (parent == null) return (float)this.area.x;
      return (float)parent.area.x + relative * (float)parent.area.w - (float)this.area.w * this.flex().x.anchor;
   }

   private float getTargetScreenY(float relative) {
      GuiElement parent = this.getParent();
      if (parent == null) return (float)this.area.y;
      return (float)parent.area.y + relative * (float)parent.area.h - (float)this.area.h * this.flex().y.anchor;
   }

   private float getFactor(long now, long startedAt, int duration) {
      if (duration <= 0) return 1.0F;
      long elapsed = Math.max(0L, now - startedAt);
      long total = (long)duration * 50000000L;
      return Math.min(1.0F, Math.max(0.0F, (float)elapsed / (float)total));
   }
}
