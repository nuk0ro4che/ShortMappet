package mchorse.mappet.api.ui.components;

import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.api.ui.utils.DiscardMethod;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.utils.Icon;
import mchorse.mclib.client.gui.utils.IconRegistry;
import mchorse.mclib.client.gui.utils.Icons;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import org.joml.Quaternionf;
import java.util.Locale;
import java.util.function.Consumer;
import mchorse.mclib.client.gui.utils.resizers.Flex.Measure;
import mchorse.mclib.utils.Interpolation;


public class UIIconComponent extends UIComponent {
   public String icon = "";
   public Integer background;
   public Integer color;

   private static final Interpolation DEFAULT_INTERPOLATION = Interpolation.SINE_INOUT;
   private static final String DEFAULT_INTERPOLATION_KEY = "sine_inout";

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

   public UIIconComponent background(int background) {
      this.change(new String[]{"Background"});
      this.background = background;
      return this;
   }

   public UIIconComponent icon(String icon) {
      this.change(new String[]{"Icon"});
      this.icon = icon;
      return this;
   }

   public UIIconComponent color(int color) {
      this.change(new String[]{"Color"});
      this.color = color;
      return this;
   }

   public UIIconComponent scaleTo(float scale, int duration)
   {
      return this.scaleTo(scale, scale, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIIconComponent scaleTo(float scale, int duration, String interpolation)
   {
      return this.scaleTo(scale, scale, duration, interpolation);
   }

   public UIIconComponent scaleTo(float scaleX, float scaleY, int duration)
   {
      return this.scaleTo(scaleX, scaleY, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIIconComponent scaleTo(float scaleX, float scaleY, int duration, String interpolation)
   {
      this.change("ScaleTo");
      this.hasScaleTo = true;
      this.scaleToX = scaleX;
      this.scaleToY = scaleY;
      this.scaleToDuration = Math.max(0, duration);
      this.scaleToInterpolation = this.getInterpolationKey(interpolation);

      return this;
   }

   public UIIconComponent colorTo(int color, int duration)
   {
      return this.colorTo(color, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIIconComponent colorTo(int color, int duration, String interpolation)
   {
      this.change("ColorTo");
      this.hasColorTo = true;
      this.colorTo = color;
      this.colorToDuration = Math.max(0, duration);
      this.colorToInterpolation = this.getInterpolationKey(interpolation);

      return this;
   }

   public UIIconComponent moveTo(float x, float y, int duration)
   {
      return this.moveTo(x, y, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIIconComponent moveTo(float x, float y, int duration, String interpolation)
   {
      this.change("MoveTo");
      this.hasMoveTo = true;
      this.moveToX = x;
      this.moveToY = y;
      this.moveToDuration = Math.max(0, duration);
      this.moveToInterpolation = this.getInterpolationKey(interpolation);

      return this;
   }

   public UIIconComponent rotateTo(float rotation, int duration)
   {
      return this.rotateTo(rotation, duration, DEFAULT_INTERPOLATION_KEY);
   }

   public UIIconComponent rotateTo(float rotation, int duration, String interpolation)
   {
      this.change("RotateTo");
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
         return Interpolation.valueOf(
                 interpolation.trim().toUpperCase(Locale.ROOT).replace('-', '_')
         ).name().toLowerCase(Locale.ROOT);
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
         return Interpolation.valueOf(
                 interpolation.trim().toUpperCase(Locale.ROOT).replace('-', '_')
         );
      }
      catch (IllegalArgumentException exception)
      {
         return DEFAULT_INTERPOLATION;
      }
   }


   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected boolean isDataReserved() {
      return true;
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   protected void applyProperty(UIContext context, String key, GuiElement element) {
      super.applyProperty(context, key, element);
      AnimatedIconElement button = (AnimatedIconElement) element;

      if (key.equals("Icon")) {
         button.both(this.getIcon());
      }
      else if (key.equals("Color")) {
         button.color(this.color);
      }
      else if (key.equals("ScaleTo")) {
         button.scaleTo(this.scaleToX, this.scaleToY, this.scaleToDuration, getInterpolation(this.scaleToInterpolation));
      }
      else if (key.equals("ColorTo")) {
         button.colorTo(this.colorTo, this.colorToDuration, getInterpolation(this.colorToInterpolation));
      }
      else if (key.equals("MoveTo")) {
         button.moveTo(this.moveToX, this.moveToY, this.moveToDuration, getInterpolation(this.moveToInterpolation));
      }
      else if (key.equals("RotateTo")) {
         button.rotateTo(this.rotateTo, this.rotateToDuration, getInterpolation(this.rotateToInterpolation));
      }
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   public GuiElement create(class_310 mc, UIContext context) {
      AnimatedIconElement button = new AnimatedIconElement(mc, this.getIcon(), (b) -> {
         if (!this.id.isEmpty()) {
            this.populateData(context.data);
            context.dirty(this.id, (long)this.updateDelay);
         }

      }) {
         protected void drawSkin(GuiContext guiContext) {
            if (UIIconComponent.this.background != null) {
               this.area.draw(UIIconComponent.this.background);
            }

            super.drawSkin(guiContext);
         }
      };

      if (this.color != null) {
         button.color(this.color);
      }

      this.applyAnimations(button);
      return this.apply(button, context);
   }

   @DiscardMethod
   @Environment(EnvType.CLIENT)
   private void applyAnimations(AnimatedIconElement button)
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
   @Environment(EnvType.CLIENT)
   private Icon getIcon() {
      Icon icon = (Icon)IconRegistry.icons.get(this.icon);
      if (icon == null) {
         icon = Icons.NONE;
      }

      return icon;
   }

   @DiscardMethod
   public void populateData(class_2487 tag) {
      super.populateData(tag);
      if (!this.id.isEmpty()) {
         tag.method_10569(this.id, tag.method_10550(this.id) + 1);
      }

   }

   @DiscardMethod
   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("Icon", this.icon);
      if (this.background != null) {
         tag.method_10569("Background", this.background);
      }

      if (this.color != null) {
         tag.method_10569("Color", this.color);
      }

      if (this.hasScaleTo) {
         class_2487 scale = new class_2487();

         scale.method_10548("X", this.scaleToX);
         scale.method_10548("Y", this.scaleToY);
         scale.method_10569("Duration", this.scaleToDuration);
         scale.method_10582("Interpolation", this.scaleToInterpolation);

         tag.method_10566("ScaleTo", scale);
      }

      if (this.hasColorTo) {
         class_2487 color = new class_2487();

         color.method_10569("Color", this.colorTo);
         color.method_10569("Duration", this.colorToDuration);
         color.method_10582("Interpolation", this.colorToInterpolation);

         tag.method_10566("ColorTo", color);
      }

      if (this.hasMoveTo) {
         class_2487 move = new class_2487();

         move.method_10548("X", this.moveToX);
         move.method_10548("Y", this.moveToY);
         move.method_10569("Duration", this.moveToDuration);
         move.method_10582("Interpolation", this.moveToInterpolation);

         tag.method_10566("MoveTo", move);
      }

      if (this.hasRotateTo) {
         class_2487 rotation = new class_2487();

         rotation.method_10548("Angle", this.rotateTo);
         rotation.method_10569("Duration", this.rotateToDuration);
         rotation.method_10582("Interpolation", this.rotateToInterpolation);

         tag.method_10566("RotateTo", rotation);
      }

   }

   @DiscardMethod
   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Icon")) {
         this.icon = tag.method_10558("Icon");
      }

      if (tag.method_10545("Background")) {
         this.background = tag.method_10550("Background");
      }

      if (tag.method_10545("Color")) {
         this.color = tag.method_10550("Color");
      }

      if (tag.method_10573("ScaleTo", 10)) {
         class_2487 scale = tag.method_10562("ScaleTo");

         this.hasScaleTo = true;
         this.scaleToX = scale.method_10583("X");
         this.scaleToY = scale.method_10583("Y");
         this.scaleToDuration = scale.method_10550("Duration");
         this.scaleToInterpolation = scale.method_10545("Interpolation") ? this.getInterpolationKey(scale.method_10558("Interpolation")) : DEFAULT_INTERPOLATION_KEY;
      }

      if (tag.method_10573("ColorTo", 10)) {
         class_2487 color = tag.method_10562("ColorTo");

         this.hasColorTo = true;
         this.colorTo = color.method_10550("Color");
         this.colorToDuration = color.method_10550("Duration");
         this.colorToInterpolation = color.method_10545("Interpolation") ? this.getInterpolationKey(color.method_10558("Interpolation")) : DEFAULT_INTERPOLATION_KEY;
      }

      if (tag.method_10573("MoveTo", 10)) {
         class_2487 move = tag.method_10562("MoveTo");

         this.hasMoveTo = true;
         this.moveToX = move.method_10583("X");
         this.moveToY = move.method_10583("Y");
         this.moveToDuration = move.method_10550("Duration");
         this.moveToInterpolation = move.method_10545("Interpolation") ? this.getInterpolationKey(move.method_10558("Interpolation")) : DEFAULT_INTERPOLATION_KEY;
      }

      if (tag.method_10573("RotateTo", 10)) {
         class_2487 rotation = tag.method_10562("RotateTo");

         this.hasRotateTo = true;
         this.rotateTo = rotation.method_10583("Angle");
         this.rotateToDuration = rotation.method_10550("Duration");
         this.rotateToInterpolation = rotation.method_10545("Interpolation") ? this.getInterpolationKey(rotation.method_10558("Interpolation")) : DEFAULT_INTERPOLATION_KEY;
      }
   }

   @Environment(EnvType.CLIENT)
   private static class AnimatedIconElement extends GuiIconElement
   {
      private float scaleX = 1.0F;
      private float scaleY = 1.0F;
      private float scaleFromX;
      private float scaleFromY;
      private float scaleTargetX = 1.0F;
      private float scaleTargetY = 1.0F;
      private float scaleStartedAt = -1.0F;
      private int scaleDuration;
      private Interpolation scaleInterpolation = DEFAULT_INTERPOLATION;
      private boolean scalePending;
      private int color;
      private int colorFrom;
      private int colorTarget;
      private float colorStartedAt = -1.0F;
      private int colorDuration;
      private Interpolation colorInterpolation = DEFAULT_INTERPOLATION;
      private boolean colorPending;
      private boolean colorInitialized;
      private float moveX;
      private float moveY;
      private float moveFromX;
      private float moveFromY;
      private float moveTargetX;
      private float moveTargetY;
      private float moveStartedAt = -1.0F;
      private int moveDuration;
      private Interpolation moveInterpolation = DEFAULT_INTERPOLATION;
      private boolean movePending;
      private boolean moveInitialized;
      private float rotation;
      private float rotationFrom;
      private float rotationTarget;
      private float rotationStartedAt = -1.0F;
      private int rotationDuration;
      private Interpolation rotationInterpolation = DEFAULT_INTERPOLATION;
      private boolean rotationPending;

      private AnimatedIconElement(class_310 mc, Icon icon, Consumer<GuiIconElement> callback)
      {
         super(mc, icon, callback);
      }

      public void scaleTo(float x, float y, int duration, Interpolation interpolation)
      {
         this.scaleTargetX = x;
         this.scaleTargetY = y;
         this.scaleDuration = duration;
         this.scaleInterpolation = interpolation;
         this.scalePending = true;
      }

      public void color(int color)
      {
         this.color = this.normalizeColor(color);
         this.iconColor(this.color);
         this.hoverColor(this.color);
         this.disabledColor(this.color);
         this.colorInitialized = true;
      }

      public void colorTo(int color, int duration, Interpolation interpolation)
      {
         this.colorTarget = this.normalizeColor(color);
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

      @Override
      public void draw(GuiContext context)
      {
         this.updateAnimations(context);
         if (this.scaleX == 1.0F && this.scaleY == 1.0F && this.rotation == 0.0F)
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
         if (this.scaleX == 1.0F && this.scaleY == 1.0F && this.rotation == 0.0F)
         {
            return;
         }

         float centerX = (float)this.area.x + (float)this.area.w / 2.0F;
         float centerY = (float)this.area.y + (float)this.area.h / 2.0F;
         float dx = (float)context.mouseX - centerX;
         float dy = (float)context.mouseY - centerY;
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
         float now = (float)context.tick + context.partialTicks;
         this.startAnimations(now);
         this.updateScale(now);
         this.updateColor(now);
         this.updateMove(now);
         this.updateRotation(now);
      }

      private void startAnimations(float now)
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
               this.color = this.hover ? this.hoverColor : this.iconColor;
               this.colorInitialized = true;
            }

            this.colorFrom = this.color;
            this.colorStartedAt = now;
            this.colorPending = false;
         }

         if (this.movePending)
         {
            if (!this.moveInitialized)
            {
               this.moveX = this.getRelativeX();
               this.moveY = this.getRelativeY();
               this.moveInitialized = true;
            }

            this.moveFromX = this.moveX;
            this.moveFromY = this.moveY;
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

      private void updateScale(float now)
      {
         if (this.scaleStartedAt < 0.0F)
         {
            return;
         }

         float factor = this.getFactor(now, this.scaleStartedAt, this.scaleDuration);
         this.scaleX = this.interpolate(this.scaleFromX, this.scaleTargetX, factor, this.scaleInterpolation);
         this.scaleY = this.interpolate(this.scaleFromY, this.scaleTargetY, factor, this.scaleInterpolation);
         if (factor >= 1.0F)
         {
            this.scaleStartedAt = -1.0F;
         }
      }

      private void updateColor(float now)
      {
         if (this.colorStartedAt < 0.0F)
         {
            return;
         }

         float factor = this.getFactor(now, this.colorStartedAt, this.colorDuration);
         this.color(this.interpolateColor(this.colorFrom, this.colorTarget, factor, this.colorInterpolation));
         if (factor >= 1.0F)
         {
            this.colorStartedAt = -1.0F;
         }
      }

      private void updateMove(float now)
      {
         if (this.moveStartedAt < 0.0F)
         {
            return;
         }

         float factor = this.getFactor(now, this.moveStartedAt, this.moveDuration);
         this.moveX = this.interpolate(this.moveFromX, this.moveTargetX, factor, this.moveInterpolation);
         this.moveY = this.interpolate(this.moveFromY, this.moveTargetY, factor, this.moveInterpolation);
         this.flex().x.set(this.moveX, Measure.RELATIVE);
         this.flex().y.set(this.moveY, Measure.RELATIVE);
         this.resize();
         if (factor >= 1.0F)
         {
            this.moveStartedAt = -1.0F;
         }
      }

      private void updateRotation(float now)
      {
         if (this.rotationStartedAt < 0.0F)
         {
            return;
         }

         float factor = this.getFactor(now, this.rotationStartedAt, this.rotationDuration);
         this.rotation = this.interpolate(this.rotationFrom, this.rotationTarget, factor, this.rotationInterpolation);
         if (factor >= 1.0F)
         {
            this.rotationStartedAt = -1.0F;
         }
      }

      private float getRelativeX()
      {
         GuiElement parent = this.getParent();
         if (parent == null || parent.area.w == 0)
         {
            return 0.0F;
         }

         return ((float)this.area.x + (float)this.area.w * this.flex().x.anchor - (float)parent.area.x) / (float)parent.area.w;
      }

      private float getRelativeY()
      {
         GuiElement parent = this.getParent();
         if (parent == null || parent.area.h == 0)
         {
            return 0.0F;
         }

         return ((float)this.area.y + (float)this.area.h * this.flex().y.anchor - (float)parent.area.y) / (float)parent.area.h;
      }

      private float getFactor(float now, float startedAt, int duration)
      {
         return duration <= 0 ? 1.0F : Math.min(1.0F, Math.max(0.0F, (now - startedAt) / (float)duration));
      }

      private float interpolate(float from, float to, float factor, Interpolation interpolation)
      {
         return interpolation.interpolate(from, to, factor);
      }

      private int interpolateColor(int from, int to, float factor, Interpolation interpolation)
      {
         int alpha = this.clampColor(Math.round(this.interpolate((float)(from >>> 24), (float)(to >>> 24), factor, interpolation)));
         int red = this.clampColor(Math.round(this.interpolate((float)(from >> 16 & 255), (float)(to >> 16 & 255), factor, interpolation)));
         int green = this.clampColor(Math.round(this.interpolate((float)(from >> 8 & 255), (float)(to >> 8 & 255), factor, interpolation)));
         int blue = this.clampColor(Math.round(this.interpolate((float)(from & 255), (float)(to & 255), factor, interpolation)));

         return alpha << 24 | red << 16 | green << 8 | blue;
      }

      private int normalizeColor(int color)
      {
         return (color & -16777216) == 0 ? color | -16777216 : color;
      }

      private int clampColor(int color)
      {
         return Math.max(0, Math.min(255, color));
      }
   }
}
