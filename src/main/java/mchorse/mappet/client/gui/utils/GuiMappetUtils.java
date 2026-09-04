package mchorse.mappet.client.gui.utils;

import java.util.function.Consumer;
import mchorse.mappet.ClientProxy;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.client.gui.utils.overlays.GuiContentNamesOverlayPanel;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mappet.client.gui.utils.overlays.GuiPromptOverlayPanel;
import mchorse.mclib.McLib;
import mchorse.mclib.client.InputRenderer;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.framework.elements.context.GuiSimpleContextMenu;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.ColorUtils;
import mchorse.mclib.utils.Interpolation;
import mchorse.mclib.utils.Interpolations;
import net.minecraft.class_1074;
import net.minecraft.class_1109;
import net.minecraft.class_310;
import net.minecraft.class_3414;

public class GuiMappetUtils {
   public static GuiTextElement fullWindowContext(GuiTextElement text, IKey title) {
      class_310 mc = class_310.method_1551();
      text.context(() -> (new GuiSimpleContextMenu(mc)).action(Icons.EDIT, IKey.lang("mappet.gui.overlays.text_fullscreen"), () -> {
            GuiPromptOverlayPanel panel = new GuiPromptOverlayPanel(mc, title, text);
            GuiOverlay overlay = new GuiOverlay(mc, panel);
            panel.flex().w(1.0F, -30).h(54);
            GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay);
         }));
      return text;
   }

   public static void openPicker(IContentType type, String value, Consumer<String> callback) {
      ClientProxy.requestNames(type, (names) -> {
         GuiContentNamesOverlayPanel overlay = new GuiContentNamesOverlayPanel(class_310.method_1551(), type.getPickLabel(), type, names, callback);
         overlay.set(value);
         GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.5F, 0.7F);
      });
   }

   public static void drawRightClickHere(GuiContext context, Area area) {
      int primary = (Integer)McLib.primaryColor.get();
      double ticks = (double)((float)context.tick + context.partialTicks) % (double)80.0F;
      double factor = Math.abs(ticks / (double)80.0F * (double)2.0F - (double)1.0F);
      factor = Interpolation.EXP_INOUT.interpolate((double)0.0F, (double)1.0F, factor);
      double factor2 = Interpolations.envelope(ticks, (double)37.0F, (double)40.0F, (double)40.0F, (double)43.0F);
      factor2 = Interpolation.CUBIC_OUT.interpolate((double)0.0F, (double)1.0F, factor2);
      int offset = (int)(factor * (double)70.0F + factor2 * (double)2.0F);
      GuiDraw.drawDropCircleShadow(area.mx(), area.my() + (int)(factor * (double)70.0F), 16, 0, 16, -2013265920 + primary, primary);
      InputRenderer.renderMouseButtons(area.mx() - 6, area.my() - 8 + offset, 0, false, factor2 > (double)0.0F, false, false);
      String label = class_1074.method_4662("mappet.gui.right_click", new Object[0]);
      int w = (int)((float)area.w / 1.1F);
      int color = ColorUtils.multiplyColor(4473924, 1.0F - (float)factor);
      GuiDraw.drawMultiText(context.font, label, area.mx() - w / 2, area.my() - 20, color, w, 12, 0.5F, 1.0F);
      GuiDraw.drawVerticalGradientRect(area.x, area.my() + 20, area.ex(), area.my() + 40, 0, -16777216);
      GuiDraw.drawRect(area.x, area.my() + 40, area.ex(), area.my() + 90, -16777216);
   }

   public static GuiCirculateElement createTargetCirculate(class_310 mc, TargetMode defaultTarget, Consumer<TargetMode> callback) {
      GuiCirculateElement button = new GuiCirculateElement(mc, (b) -> {
         if (callback != null) {
            callback.accept(TargetMode.values()[b.getValue()]);
         }

      });

      for(TargetMode target : TargetMode.values()) {
         button.addLabel(IKey.lang("mappet.gui.conditions.targets." + target.name().toLowerCase()));
      }

      button.setValue(defaultTarget.ordinal());
      return button;
   }

   public static void playSound(class_3414 event) {
      class_310.method_1551().method_1483().method_4873(class_1109.method_4758(event, 1.0F));
   }
}
