package mchorse.mappet.api.ui.components;

import java.util.Locale;
import java.util.function.Consumer;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.client.gui.utils.resizers.Flex.Measure;
import mchorse.mclib.utils.Interpolation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Quaternionf;

public class UIButtonComponent extends UILabelBaseComponent
{
   private static final Interpolation DEFAULT_INTERPOLATION = Interpolation.SINE_INOUT;
   private static final String DEFAULT_INTERPOLATION_KEY = "sine_inout";

   public Integer background;
   public boolean hoverEvents;
   public boolean unhoverEvents;
   private boolean hasScaleTo;
   private float scaleToX = 1.0F;
   private float scaleToY = 1.0F;
   private int scaleToDuration;
   private String scaleToInterpolation = DEFAULT_INTERPOLATION_KEY;
   private boolean hasColorTo;
   private int colorTo;
   private int colorToDuration;
   private String colorToInterpolation = DEFAULT_INTERPOLATION_KEY;
   private boolean hasMoveTo;
   private float moveToX;
   private float moveToY;
   private int moveToDuration;
   private String moveToInterpolation = DEFAULT_INTERPOLATION_KEY;
   private boolean hasRotateTo;
   private float rotateTo;
   private int rotateToDuration;
   private String rotateToInterpolation = DEFAULT_INTERPOLATION_KEY;

   public UIButtonComponent background(int background)
   {
      this.change(new String[]{"Background"});
      this.background = background;
      return this;
   }

   public UIButtonComponent noBackground()
   {
      this.hasBackground = false;
      return this;
   }

   public UIButtonComponent scaleTo(float scale, int duration)
   {
      return this.scaleTo(scale, scale, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIButtonComponent scaleTo(float scale, int duration, String interpolation)
   {
      return this.scaleTo(scale, scale, duration, interpolation);
   }

   public UIButtonComponent scaleTo(float scaleX, float scaleY, int duration)
   {
      return this.scaleTo(scaleX, scaleY, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIButtonComponent scaleTo(float scaleX, float scaleY, int duration, String interpolation)
   {
      this.change(new String[]{"ScaleTo"});
      this.hasScaleTo = true;
      this.scaleToX = scaleX;
      this.scaleToY = scaleY;
      this.scaleToDuration = Math.max(0, duration);
      this.scaleToInterpolation = this.getInterpolationKey(interpolation);
      return this;
   }

   public UIButtonComponent colorTo(int color, int duration)
   {
      return this.colorTo(color, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIButtonComponent colorTo(int color, int duration, String interpolation)
   {
      this.change(new String[]{"ColorTo"});
      this.hasColorTo = true;
      this.colorTo = color;
      this.colorToDuration = Math.max(0, duration);
      this.colorToInterpolation = this.getInterpolationKey(interpolation);
      return this;
   }

   public UIButtonComponent moveTo(float x, float y, int duration)
   {
      return this.moveTo(x, y, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIButtonComponent moveTo(float x, float y, int duration, String interpolation)
   {
      this.change(new String[]{"MoveTo"});
      this.hasMoveTo = true;
      this.moveToX = x;
      this.moveToY = y;
      this.moveToDuration = Math.max(0, duration);
      this.moveToInterpolation = this.getInterpolationKey(interpolation);
      return this;
   }

   public UIButtonComponent rotateTo(float rotation, int duration)
   {
      return this.rotateTo(rotation, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIButtonComponent rotateTo(float rotation, int duration, String interpolation)
   {
      this.change(new String[]{"RotateTo"});
      this.hasRotateTo = true;
      this.rotateTo = rotation;
      this.rotateToDuration = Math.max(0, duration);
      this.rotateToInterpolation = this.getInterpolationKey(interpolation);
      return this;
   }

   @DiscardMethod
   private String getInterpolationKey(String interpolation)
   {
      if (interpolation == null || interpolation.trim().isEmpty())
      {
         return DEFAULT_INTERPOLATION_KEY;
      }

      try
      {
         return Interpolation.valueOf(interpolation.trim().toUpperCase(Locale.ROOT).replace('-', '_')).name().toLowerCase(Locale.ROOT);
      }
      catch (IllegalArgumentException exception)
      {
         return DEFAULT_INTERPOLATION_KEY;
      }
   }

   @DiscardMethod
   private static Interpolation getInterpolation(String interpolation)
   {
      if (interpolation == null || interpolation.trim().isEmpty())
      {
         return DEFAULT_INTERPOLATION;
      }

      try
      {
         return Interpolation.valueOf(interpolation.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
      }
      catch (IllegalArgumentException exception)
      {
         return DEFAULT_INTERPOLATION;
      }
   }

   public UIButtonComponent enterHover()
   {
      this.change(new String[]{"HoverEvents"});
      this.hoverEvents = true;
      return this;
   }

   public UIButtonComponent exitHover()
   {
      this.change(new String[]{"UnhoverEvents"});
      this.unhoverEvents = true;
      return this;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected boolean isDataReserved()
   {
      return true;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element)
   {
      super.applyProperty(context, key, element);
      AnimatedButtonElement button = (AnimatedButtonElement)element;
      if (key.equals("Label"))
      {
         button.label = IKey.str(this.getLabel());
      }
      else if (key.equals("Background"))
      {
         if (this.background != null && this.background >= 0)
         {
            button.color(this.background);
         }
         else
         {
            button.custom = false;
         }
      }
      else if (key.equals("ScaleTo"))
      {
         button.scaleTo(this.scaleToX, this.scaleToY, this.scaleToDuration, getInterpolation(this.scaleToInterpolation));
      }
      else if (key.equals("ColorTo"))
      {
         button.colorTo(this.colorTo, this.colorToDuration, getInterpolation(this.colorToInterpolation));
      }
      else if (key.equals("MoveTo"))
      {
         button.moveTo(this.moveToX, this.moveToY, this.moveToDuration, getInterpolation(this.moveToInterpolation));
      }
      else if (key.equals("RotateTo"))
      {
         button.rotateTo(this.rotateTo, this.rotateToDuration, getInterpolation(this.rotateToInterpolation));
      }
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context)
   {
      AnimatedButtonElement button = new AnimatedButtonElement(mc, IKey.str(this.getLabel()), (b) -> {
         if (!this.id.isEmpty())
         {
            this.populateData(context.data);
            context.dirty(this.id, (long)this.updateDelay);
         }
      })
      {
         private boolean wasHovered;

         public void draw(GuiContext guiContext)
         {
            super.draw(guiContext);
            if (!UIButtonComponent.this.id.isEmpty())
            {
               if (UIButtonComponent.this.hoverEvents && this.hover && !this.wasHovered)
               {
                  context.sendHover(UIButtonComponent.this.id);
               }

               if (UIButtonComponent.this.unhoverEvents && !this.hover && this.wasHovered)
               {
                  context.sendUnhover(UIButtonComponent.this.id);
               }
            }

            this.wasHovered = this.hover;
         }
      };

      if (this.background != null && this.background >= 0)
      {
         button.color(this.background);
      }

      button.background(this.hasBackground);
      this.applyAnimations(button);
      return this.apply(button, context);
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private void applyAnimations(AnimatedButtonElement button)
   {
      if (this.hasScaleTo)
      {
         button.scaleTo(this.scaleToX, this.scaleToY, this.scaleToDuration, getInterpolation(this.scaleToInterpolation));
      }

      if (this.hasColorTo)
      {
         button.colorTo(this.colorTo, this.colorToDuration, getInterpolation(this.colorToInterpolation));
      }

      if (this.hasMoveTo)
      {
         button.moveTo(this.moveToX, this.moveToY, this.moveToDuration, getInterpolation(this.moveToInterpolation));
      }

      if (this.hasRotateTo)
      {
         button.rotateTo(this.rotateTo, this.rotateToDuration, getInterpolation(this.rotateToInterpolation));
      }
   }

   @DiscardMethod
   public void populateData(class_2487 tag)
   {
      super.populateData(tag);
      if (!this.id.isEmpty())
      {
         tag.method_10569(this.id, tag.method_10550(this.id) + 1);
      }
   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag)
   {
      super.serializeNBT(tag);
      if (this.background != null)
      {
         tag.method_10569("Background", this.background);
      }

      if (this.hoverEvents)
      {
         tag.method_10556("HoverEvents", true);
      }

      if (this.unhoverEvents)
      {
         tag.method_10556("UnhoverEvents", true);
      }

      if (this.hasScaleTo)
      {
         class_2487 scale = new class_2487();
         scale.method_10548("X", this.scaleToX);
         scale.method_10548("Y", this.scaleToY);
         scale.method_10569("Duration", this.scaleToDuration);
         scale.method_10582("Interpolation", this.scaleToInterpolation);
         tag.method_10566("ScaleTo", scale);
      }

      if (this.hasColorTo)
      {
         class_2487 color = new class_2487();
         color.method_10569("Color", this.colorTo);
         color.method_10569("Duration", this.colorToDuration);
         color.method_10582("Interpolation", this.colorToInterpolation);
         tag.method_10566("ColorTo", color);
      }

      if (this.hasMoveTo)
      {
         class_2487 move = new class_2487();
         move.method_10548("X", this.moveToX);
         move.method_10548("Y", this.moveToY);
         move.method_10569("Duration", this.moveToDuration);
         move.method_10582("Interpolation", this.moveToInterpolation);
         tag.method_10566("MoveTo", move);
      }

      if (this.hasRotateTo)
      {
         class_2487 rotation = new class_2487();
         rotation.method_10548("Angle", this.rotateTo);
         rotation.method_10569("Duration", this.rotateToDuration);
         rotation.method_10582("Interpolation", this.rotateToInterpolation);
         tag.method_10566("RotateTo", rotation);
      }
   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag)
   {
      super.deserializeNBT(tag);
      if (tag.method_10545("Background"))
      {
         this.background = tag.method_10550("Background");
      }

      if (tag.method_10545("HoverEvents"))
      {
         this.hoverEvents = tag.method_10577("HoverEvents");
      }

      if (tag.method_10545("UnhoverEvents"))
      {
         this.unhoverEvents = tag.method_10577("UnhoverEvents");
      }

      if (tag.method_10573("ScaleTo", 10))
      {
         class_2487 scale = tag.method_10562("ScaleTo");
         this.hasScaleTo = true;
         this.scaleToX = scale.method_10583("X");
         this.scaleToY = scale.method_10583("Y");
         this.scaleToDuration = scale.method_10550("Duration");
         this.scaleToInterpolation = scale.method_10545("Interpolation") ? this.getInterpolationKey(scale.method_10558("Interpolation")) : DEFAULT_INTERPOLATION_KEY;
      }

      if (tag.method_10573("ColorTo", 10))
      {
         class_2487 color = tag.method_10562("ColorTo");
         this.hasColorTo = true;
         this.colorTo = color.method_10550("Color");
         this.colorToDuration = color.method_10550("Duration");
         this.colorToInterpolation = color.method_10545("Interpolation") ? this.getInterpolationKey(color.method_10558("Interpolation")) : DEFAULT_INTERPOLATION_KEY;
      }

      if (tag.method_10573("MoveTo", 10))
      {
         class_2487 move = tag.method_10562("MoveTo");
         this.hasMoveTo = true;
         this.moveToX = move.method_10583("X");
         this.moveToY = move.method_10583("Y");
         this.moveToDuration = move.method_10550("Duration");
         this.moveToInterpolation = move.method_10545("Interpolation") ? this.getInterpolationKey(move.method_10558("Interpolation")) : DEFAULT_INTERPOLATION_KEY;
      }

      if (tag.method_10573("RotateTo", 10))
      {
         class_2487 rotation = tag.method_10562("RotateTo");
         this.hasRotateTo = true;
         this.rotateTo = rotation.method_10583("Angle");
         this.rotateToDuration = rotation.method_10550("Duration");
         this.rotateToInterpolation = rotation.method_10545("Interpolation") ? this.getInterpolationKey(rotation.method_10558("Interpolation")) : DEFAULT_INTERPOLATION_KEY;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class AnimatedButtonElement extends GuiButtonElement
   {
      private float scaleX = 1.0F;
      private float scaleY = 1.0F;
      private float scaleFromX;
      private float scaleFromY;
      private float scaleTargetX = 1.0F;
      private float scaleTargetY = 1.0F;
      private long scaleStartedAt = -1L;
      private int scaleDuration;
      private Interpolation scaleInterpolation = DEFAULT_INTERPOLATION;
      private boolean scalePending;
      private int color;
      private int colorFrom;
      private int colorTarget;
      private long colorStartedAt = -1L;
      private int colorDuration;
      private Interpolation colorInterpolation = DEFAULT_INTERPOLATION;
      private boolean colorPending;
      private boolean colorInitialized;
      private float moveTargetX;
      private float moveTargetY;
      private float moveFromScreenX;
      private float moveFromScreenY;
      private float moveTargetScreenX;
      private float moveTargetScreenY;
      private float moveOffsetX;
      private float moveOffsetY;
      private long moveStartedAt = -1L;
      private int moveDuration;
      private Interpolation moveInterpolation = DEFAULT_INTERPOLATION;
      private boolean movePending;
      private float rotation;
      private float rotationFrom;
      private float rotationTarget;
      private long rotationStartedAt = -1L;
      private int rotationDuration;
      private Interpolation rotationInterpolation = DEFAULT_INTERPOLATION;
      private boolean rotationPending;

      private AnimatedButtonElement(class_310 mc, IKey label, Consumer<GuiButtonElement> callback)
      {
         super(mc, label, callback);
      }

      public void scaleTo(float x, float y, int duration, Interpolation interpolation)
      {
         this.scaleTargetX = x;
         this.scaleTargetY = y;
         this.scaleDuration = duration;
         this.scaleInterpolation = interpolation;
         this.scalePending = true;
      }

      public void colorTo(int color, int duration, Interpolation interpolation)
      {
         this.colorTarget = color & 16777215;
         this.colorDuration = duration;
         this.colorInterpolation = interpolation;
         this.colorPending = true;
      }

      public void moveTo(float x, float y, int duration, Interpolation interpolation)
      {
         this.moveTargetX = x;
         this.moveTargetY = y;
         this.moveDuration = duration;
         this.moveInterpolation = interpolation;
         this.movePending = true;
      }

      public void rotateTo(float rotation, int duration, Interpolation interpolation)
      {
         this.rotationTarget = rotation;
         this.rotationDuration = duration;
         this.rotationInterpolation = interpolation;
         this.rotationPending = true;
      }

      public void draw(GuiContext context)
      {
         this.updateAnimations(context);
         if (this.moveOffsetX == 0.0F && this.moveOffsetY == 0.0F && this.scaleX == 1.0F && this.scaleY == 1.0F && this.rotation == 0.0F)
         {
            super.draw(context);
            return;
         }

         float centerX = (float)this.area.x + (float)this.area.w / 2.0F;
         float centerY = (float)this.area.y + (float)this.area.h / 2.0F;
         int mouseX = context.mouseX;
         int mouseY = context.mouseY;
         class_4587 matrices = context.drawContext.method_51448();
         matrices.method_22903();
         try
         {
            matrices.method_22904((double)this.moveOffsetX, (double)this.moveOffsetY, 0.0D);
            matrices.method_22904((double)centerX, (double)centerY, 0.0D);
            matrices.method_22907((new Quaternionf()).rotationAxis((float)Math.toRadians((double)this.rotation), 0.0F, 0.0F, 1.0F));
            matrices.method_22905(this.scaleX, this.scaleY, 1.0F);
            matrices.method_22904((double)(-centerX), (double)(-centerY), 0.0D);
            this.remapPointer(context);
            super.draw(context);
         }
         finally
         {
            context.mouseX = mouseX;
            context.mouseY = mouseY;
            matrices.method_22909();
         }
      }

      public boolean mouseClicked(GuiContext context)
      {
         int mouseX = context.mouseX;
         int mouseY = context.mouseY;
         try
         {
            this.remapPointer(context);
            return super.mouseClicked(context);
         }
         finally
         {
            context.mouseX = mouseX;
            context.mouseY = mouseY;
         }
      }

      
      private void remapPointer(GuiContext context)
      {
         if (this.moveOffsetX == 0.0F && this.moveOffsetY == 0.0F && this.scaleX == 1.0F && this.scaleY == 1.0F && this.rotation == 0.0F)
         {
            return;
         }

         float centerX = (float)this.area.x + (float)this.area.w / 2.0F;
         float centerY = (float)this.area.y + (float)this.area.h / 2.0F;
         float dx = (float)context.mouseX - (centerX + this.moveOffsetX);
         float dy = (float)context.mouseY - (centerY + this.moveOffsetY);
         double radians = Math.toRadians((double)(-this.rotation));
         float cos = (float)Math.cos(radians);
         float sin = (float)Math.sin(radians);
         float localX = dx * cos - dy * sin;
         float localY = dx * sin + dy * cos;
         float safeScaleX = Math.abs(this.scaleX) < 0.0001F ? 0.0001F : this.scaleX;
         float safeScaleY = Math.abs(this.scaleY) < 0.0001F ? 0.0001F : this.scaleY;
         context.mouseX = Math.round(centerX + localX / safeScaleX);
         context.mouseY = Math.round(centerY + localY / safeScaleY);
      }

      private void updateAnimations(GuiContext context)
      {
         long now = System.nanoTime();
         this.startAnimations(now);
         this.updateScale(now);
         this.updateColor(now);
         this.updateMove(now);
         this.updateRotation(now);
      }

      private void startAnimations(long now)
      {
         if (this.scalePending)
         {
            this.scaleFromX = this.scaleX;
            this.scaleFromY = this.scaleY;
            this.scaleStartedAt = now;
            this.scalePending = false;
         }

         if (this.colorPending)
         {
            if (!this.colorInitialized)
            {
               this.color = this.custom ? this.customColor : (Integer)McLib.primaryColor.get();
               this.colorInitialized = true;
            }

            this.colorFrom = this.color;
            this.colorStartedAt = now;
            this.colorPending = false;
         }

         if (this.movePending)
         {
            this.moveFromScreenX = (float)this.area.x + this.moveOffsetX;
            this.moveFromScreenY = (float)this.area.y + this.moveOffsetY;
            this.moveTargetScreenX = this.getTargetScreenX(this.moveTargetX);
            this.moveTargetScreenY = this.getTargetScreenY(this.moveTargetY);
            this.moveStartedAt = now;
            this.movePending = false;
         }

         if (this.rotationPending)
         {
            this.rotationFrom = this.rotation;
            this.rotationStartedAt = now;
            this.rotationPending = false;
         }
      }

      private void updateScale(long now)
      {
         if (this.scaleStartedAt < 0L)
         {
            return;
         }

         float factor = this.getFactor(now, this.scaleStartedAt, this.scaleDuration);
         this.scaleX = this.interpolate(this.scaleFromX, this.scaleTargetX, factor, this.scaleInterpolation);
         this.scaleY = this.interpolate(this.scaleFromY, this.scaleTargetY, factor, this.scaleInterpolation);
         if (factor >= 1.0F)
         {
            this.scaleStartedAt = -1L;
         }
      }

      private void updateColor(long now)
      {
         if (this.colorStartedAt < 0L)
         {
            return;
         }

         float factor = this.getFactor(now, this.colorStartedAt, this.colorDuration);
         this.color = this.interpolateColor(this.colorFrom, this.colorTarget, factor, this.colorInterpolation);
         this.color(this.color);
         if (factor >= 1.0F)
         {
            this.colorStartedAt = -1L;
         }
      }

      private void updateMove(long now)
      {
         if (this.moveStartedAt < 0L)
         {
            return;
         }

         float factor = this.getFactor(now, this.moveStartedAt, this.moveDuration);
         float currentX = this.interpolate(this.moveFromScreenX, this.moveTargetScreenX, factor, this.moveInterpolation);
         float currentY = this.interpolate(this.moveFromScreenY, this.moveTargetScreenY, factor, this.moveInterpolation);
         this.moveOffsetX = currentX - (float)this.area.x;
         this.moveOffsetY = currentY - (float)this.area.y;
         if (factor >= 1.0F)
         {
            this.flex().x.set(this.moveTargetX, Measure.RELATIVE);
            this.flex().y.set(this.moveTargetY, Measure.RELATIVE);
            this.resize();
            this.moveOffsetX = 0.0F;
            this.moveOffsetY = 0.0F;
            this.moveStartedAt = -1L;
         }
      }

      private float getTargetScreenX(float relative)
      {
         GuiElement parent = this.getParent();
         return parent == null ? (float)this.area.x : (float)parent.area.x + relative * (float)parent.area.w - (float)this.area.w * this.flex().x.anchor;
      }

      private float getTargetScreenY(float relative)
      {
         GuiElement parent = this.getParent();
         return parent == null ? (float)this.area.y : (float)parent.area.y + relative * (float)parent.area.h - (float)this.area.h * this.flex().y.anchor;
      }

      private void updateRotation(long now)
      {
         if (this.rotationStartedAt < 0L)
         {
            return;
         }

         float factor = this.getFactor(now, this.rotationStartedAt, this.rotationDuration);
         this.rotation = this.interpolate(this.rotationFrom, this.rotationTarget, factor, this.rotationInterpolation);
         if (factor >= 1.0F)
         {
            this.rotationStartedAt = -1L;
         }
      }

      private float getFactor(long now, long startedAt, int duration)
      {
         if (duration <= 0) return 1.0F;
         long elapsed = Math.max(0L, now - startedAt);
         long total = (long)duration * 50000000L;
         return Math.min(1.0F, Math.max(0.0F, (float)elapsed / (float)total));
      }

      private float interpolate(float from, float to, float factor, Interpolation interpolation)
      {
         return interpolation.interpolate(from, to, factor);
      }

      private int interpolateColor(int from, int to, float factor, Interpolation interpolation)
      {
         int red = this.clampColor(Math.round(this.interpolate((float)(from >> 16 & 255), (float)(to >> 16 & 255), factor, interpolation)));
         int green = this.clampColor(Math.round(this.interpolate((float)(from >> 8 & 255), (float)(to >> 8 & 255), factor, interpolation)));
         int blue = this.clampColor(Math.round(this.interpolate((float)(from & 255), (float)(to & 255), factor, interpolation)));
         return red << 16 | green << 8 | blue;
      }

      private int clampColor(int color)
      {
         return Math.max(0, Math.min(255, color));
      }

   }
}
