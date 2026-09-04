package mchorse.mappet.client.gui;

import java.util.Locale;
import mchorse.mappet.api.ui.UI;
import mchorse.mappet.api.ui.UIContext;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.ui.PacketUI;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.CompoundKey;
import mchorse.mclib.client.gui.utils.keys.LangKey;
import mchorse.mclib.utils.Interpolation;
import net.minecraft.class_2487;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;

public class GuiUserInterface extends GuiBase {
   private static final Interpolation DEFAULT_INTERPOLATION = Interpolation.SINE_INOUT;
   private static final float MIN_SCALE = 0.92F;
   private static final int MAX_FADE_ALPHA = 255;
   private static final int SCREEN_FADE_DURATION = 8;
   private UIContext context;
   private float enterScreenFadeStartedAt = -1.0F;
   private float enterMenuStartedAt = -1.0F;
   private float exitStartedAt = -1.0F;
   private boolean enterFinished;
   private boolean exitRequested;
   private boolean closing;

   public GuiUserInterface(class_310 mc, UI ui) {
      this.context = new UIContext(ui);
      GuiElement element = ui.root.create(mc, this.context);
      element.flex().relative(this.root).wh(1.0F, 1.0F);
      this.root.add(element);
      this.enterFinished = ui.enterDuration <= 0;
   }

   public void handleUIChanges(class_2487 data) {
      for(String key : data.method_10541()) {
         class_2487 tag = data.method_10562(key);
         GuiElement element = this.context.getElement(key);
         this.context.getById(key).handleChanges(this.context, tag, element);
      }

      this.root.resize();
   }

   public boolean method_25421() {
      return this.context.ui.paused;
   }

   public void requestClose() {
      this.requestClose(true);
   }

   private void requestClose(boolean force) {
      if (this.closing || this.exitRequested || !force && !this.context.ui.closable) {
         return;
      }

      if (this.context.ui.exitDuration <= 0) {
         this.finishClose(false);
         return;
      }

      this.exitRequested = true;
   }

   protected void closeScreen() {
      this.requestClose(false);
   }

   public void method_25432() {
      super.method_25432();
      if (this.context.isDirtyInProgress()) {
         this.context.sendToPlayer();
      }

      Dispatcher.sendToServer(new PacketUI(new UI(this.context.ui.getUIId())));
   }

   public boolean method_25402(double mouseX, double mouseY, int button) {
      return this.exitRequested || super.method_25402(mouseX, mouseY, button);
   }

   public boolean method_25401(double mouseX, double mouseY, double amount) {
      return this.exitRequested || super.method_25401(mouseX, mouseY, amount);
   }

   public boolean method_25406(double mouseX, double mouseY, int button) {
      return this.exitRequested || super.method_25406(mouseX, mouseY, button);
   }

   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
      return this.exitRequested || super.method_25404(keyCode, scanCode, modifiers);
   }

   public boolean method_16803(int keyCode, int scanCode, int modifiers) {
      return this.exitRequested || super.method_16803(keyCode, scanCode, modifiers);
   }

   public boolean method_25400(char character, int modifiers) {
      return this.exitRequested || super.method_25400(character, modifiers);
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      if (this.context.isDirty()) {
         this.context.sendToServer();
      }

      float now = this.getTime();
      this.updateEnterAnimation(now);
      float scale = this.getScale(now);
      float fade = this.getFade(now);

      if (this.shouldDrawMenu()) {
         class_4587 matrices = drawContext.method_51448();
         float centerX = (float)this.field_22789 / 2.0F;
         float centerY = (float)this.field_22790 / 2.0F;

         matrices.method_22903();
         try {
            matrices.method_22904((double)centerX, (double)centerY, 0.0D);
            matrices.method_22905(scale, scale, 1.0F);
            matrices.method_22904((double)(-centerX), (double)(-centerY), 0.0D);

            if (this.context.ui.background) {
               this.method_25420(drawContext);
            }

            this.renderInterface(drawContext, mouseX, mouseY, partialTicks);
         } finally {
            matrices.method_22909();
         }
      }

      this.drawFade(drawContext, fade);

      if (this.exitRequested && this.exitStartedAt >= 0.0F && this.getFactor(now, this.exitStartedAt, this.context.ui.exitDuration) >= 1.0F) {
         this.finishClose(true);
      }
   }

   private void renderInterface(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      LangKey.lastTime++;
      CompoundKey.lastTime++;
      super.context.drawContext = drawContext;
      GuiDraw.bindDrawContext(drawContext);
      GuiDraw.flush();
      GuiDraw.clearDepth();
      super.context.setMouse(mouseX, mouseY);
      super.context.partialTicks = partialTicks;

      if (!this.root.isVisible()) {
         return;
      }

      super.context.reset();
      super.context.pushViewport(this.viewport);
      try {
         this.root.draw(super.context);
      } finally {
         super.context.popViewport();
      }

      super.context.postRenderCallbacks.forEach((callback) -> callback.accept(super.context));
      GuiDraw.flush();
      GuiDraw.clearDepth();
      super.context.drawTooltip();
   }

   private void updateEnterAnimation(float now) {
      if (this.enterFinished || this.exitRequested) {
         return;
      }

      if (this.enterScreenFadeStartedAt < 0.0F) {
         this.enterScreenFadeStartedAt = now;
      }

      if (this.enterMenuStartedAt < 0.0F) {
         float factor = this.getFactor(now, this.enterScreenFadeStartedAt, SCREEN_FADE_DURATION);
         if (factor >= 1.0F) {
            this.enterMenuStartedAt = now;
         }

         return;
      }

      float factor = this.getFactor(now, this.enterMenuStartedAt, this.context.ui.enterDuration);
      if (factor >= 1.0F) {
         this.enterFinished = true;
      }
   }

   private boolean shouldDrawMenu() {
      return this.exitRequested || this.enterFinished || this.enterMenuStartedAt >= 0.0F;
   }

   private float getScale(float now) {
      if (this.exitRequested) {
         if (this.exitStartedAt < 0.0F) {
            this.exitStartedAt = now;
         }

         float factor = this.getFactor(now, this.exitStartedAt, this.context.ui.exitDuration);
         return this.interpolate(1.0F, MIN_SCALE, factor, this.context.ui.exitInterpolation);
      }

      if (!this.enterFinished && this.enterMenuStartedAt >= 0.0F) {
         float factor = this.getFactor(now, this.enterMenuStartedAt, this.context.ui.enterDuration);
         return this.interpolate(MIN_SCALE, 1.0F, factor, this.context.ui.enterInterpolation);
      }

      return this.enterFinished ? 1.0F : MIN_SCALE;
   }

   private float getFade(float now) {
      if (this.exitRequested) {
         float factor = this.getFactor(now, this.exitStartedAt, this.context.ui.exitDuration);
         return this.interpolate(0.0F, (float)MAX_FADE_ALPHA, factor, this.context.ui.exitInterpolation);
      }

      if (!this.enterFinished) {
         if (this.enterMenuStartedAt < 0.0F) {
            float factor = this.getFactor(now, this.enterScreenFadeStartedAt, SCREEN_FADE_DURATION);
            return this.interpolate(0.0F, (float)MAX_FADE_ALPHA, factor, this.context.ui.enterInterpolation);
         }

         float factor = this.getFactor(now, this.enterMenuStartedAt, this.context.ui.enterDuration);
         return this.interpolate((float)MAX_FADE_ALPHA, 0.0F, factor, this.context.ui.enterInterpolation);
      }

      return 0.0F;
   }

   private void drawFade(class_332 drawContext, float fade) {
      int alpha = Math.max(0, Math.min(255, Math.round(fade)));
      if (alpha > 0) {
         drawContext.method_25294(0, 0, this.field_22789, this.field_22790, alpha << 24);
      }
   }

   private float getTime() {
      return (float)super.context.tick + super.context.partialTicks;
   }

   private float getFactor(float now, float startedAt, int duration) {
      return duration <= 0 ? 1.0F : Math.min(1.0F, Math.max(0.0F, (now - startedAt) / (float)duration));
   }

   private float interpolate(float from, float to, float factor, String interpolation) {
      return this.getInterpolation(interpolation).interpolate(from, to, factor);
   }

   private Interpolation getInterpolation(String interpolation) {
      if (interpolation == null || interpolation.trim().isEmpty()) {
         return DEFAULT_INTERPOLATION;
      }

      try {
         return Interpolation.valueOf(interpolation.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
      } catch (IllegalArgumentException exception) {
         return DEFAULT_INTERPOLATION;
      }
   }

   private void finishClose(boolean fadeToWorld) {
      if (this.closing) {
         return;
      }

      this.closing = true;
      if (fadeToWorld) {
         this.field_22787.method_1507(new GuiUserInterfaceFadeScreen(this.field_22787, SCREEN_FADE_DURATION, this.getInterpolation(this.context.ui.exitInterpolation)));
      } else {
         super.closeScreen();
      }
   }
}
