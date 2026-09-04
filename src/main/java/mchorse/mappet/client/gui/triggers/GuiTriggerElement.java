package mchorse.mappet.client.gui.triggers;

import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiTriggerElement extends GuiElement {
   public GuiButtonElement open;
   private Trigger trigger;
   private Runnable onClose;

   public GuiTriggerElement(class_310 mc) {
      this(mc, (Trigger)null);
   }

   public GuiTriggerElement(class_310 mc, Trigger trigger) {
      super(mc);
      this.open = new GuiButtonElement(mc, IKey.lang("mappet.gui.trigger.edit"), (b) -> this.openTriggerEditor());
      this.open.flex().relative(this).wh(1.0F, 1.0F);
      this.flex().h(20);
      this.add(this.open);
      this.set(trigger);
   }

   public GuiTriggerElement onClose(Runnable onClose) {
      this.onClose = onClose;
      return this;
   }

   private void openTriggerEditor() {
      GuiTriggerOverlayPanel panel = new GuiTriggerOverlayPanel(this.mc, this.trigger, () -> {
         this.updateTooltip();
         if (this.onClose != null) {
            this.onClose.run();
         }

      });
      GuiOverlay.addOverlay(GuiBase.getCurrent(), panel, 0.55F, 0.75F);
   }

   private void updateTooltip() {
      if (this.trigger == null) {
         this.tooltip = null;
      } else {
         this.tooltip(IKey.format("mappet.gui.trigger.quantity", new Object[]{this.trigger.blocks.size()}));
      }

   }

   public Trigger get() {
      return this.trigger;
   }

   public void set(Trigger trigger) {
      this.trigger = trigger;
      this.updateTooltip();
   }
}
