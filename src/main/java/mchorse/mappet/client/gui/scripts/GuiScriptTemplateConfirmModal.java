package mchorse.mappet.client.gui.scripts;

import java.util.List;
import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.LegacyKeyCodes;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiScriptTemplateConfirmModal extends GuiModal {
   private static final int OVERLAY = 0xA0000000;
   private static final int CARD = 0xFF17191D;
   private static final int BORDER = 0xFF4B5563;
   private static final int ACCEPT = 0xFF880000;
   private static final int CANCEL = 0xFF353940;
   private final GuiElement card;
   private final GuiButtonElement replace;
   private final GuiButtonElement cancel;
   private final Consumer<Boolean> callback;

   public GuiScriptTemplateConfirmModal(class_310 mc, IKey label, Consumer<Boolean> callback) {
      super(mc, label);
      this.callback = callback;
      this.card = new GuiElement(mc);
      this.card.flex().relative(this).xy(0.5F, 0.5F).wh(360, 132).anchor(0.5F, 0.5F);
      this.remove(this.bar);
      this.card.add(this.bar);
      this.add(this.card);
      this.replace = new GuiButtonElement(mc, IKey.lang("mappet.gui.scripts.templates.replace"), (button) -> this.close(true));
      this.cancel = new GuiButtonElement(mc, IKey.lang("mappet.gui.scripts.templates.cancel"), (button) -> this.close(false));
      this.replace.color(ACCEPT).textColor(0xFFFFFF, true);
      this.cancel.color(CANCEL).textColor(0xFFFFFF, true);
      this.bar.flex().relative(this.card).y(1.0F).w(1.0F).h(30).anchorY(1.0F).row(8).padding(10);
      this.bar.add(this.replace, this.cancel);
   }

   public void close(boolean confirmed) {
      if (this.callback != null) {
         this.callback.accept(confirmed);
      }

      this.removeFromParent();
   }

   public boolean mouseClicked(GuiContext context) {
      super.mouseClicked(context);
      return true;
   }

   public boolean mouseScrolled(GuiContext context) {
      super.mouseScrolled(context);
      return true;
   }

   public void mouseReleased(GuiContext context) {
      super.mouseReleased(context);
   }

   public boolean keyTyped(GuiContext context) {
      if (context.keyCode == LegacyKeyCodes.KEY_RETURN) {
         this.replace.clickItself(context);
         return true;
      }

      if (context.keyCode == LegacyKeyCodes.KEY_ESCAPE) {
         this.cancel.clickItself(context);
         return true;
      }

      super.keyTyped(context);
      return true;
   }

   public void draw(GuiContext context) {
      GuiDraw.drawRect(this.area.x, this.area.y, this.area.ex(), this.area.ey(), OVERLAY);
      GuiDraw.drawDropShadow(this.card.area.x, this.card.area.y, this.card.area.ex(), this.card.area.ey(), 6, 0xAA000000, 0xFF000000);
      GuiDraw.drawRect(this.card.area.x, this.card.area.y, this.card.area.ex(), this.card.area.ey(), CARD);
      GuiDraw.drawRect(this.card.area.x, this.card.area.y, this.card.area.ex(), this.card.area.y + 1, BORDER);
      GuiDraw.drawRect(this.card.area.x, this.card.area.ey() - 1, this.card.area.ex(), this.card.area.ey(), BORDER);
      GuiDraw.drawRect(this.card.area.x, this.card.area.y, this.card.area.x + 1, this.card.area.ey(), BORDER);
      GuiDraw.drawRect(this.card.area.ex() - 1, this.card.area.y, this.card.area.ex(), this.card.area.ey(), BORDER);
      List<String> lines = GuiDraw.listFormattedStringToWidth(this.label.get(), this.card.area.w - 28, (text) -> GuiDraw.textWidth(this.font, text));
      int y = this.card.area.y + 18;

      for(String line : lines) {
         GuiDraw.drawStringWithShadow(this.font, line, this.card.area.x + 14, y, 0xFFFFFF);
         y += 12;
      }

      this.card.draw(context);
   }
}
