package mchorse.mappet.client.gui.triggers.panels;

import mchorse.mappet.api.triggers.blocks.EventTriggerBlock;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.triggers.GuiTriggerOverlayPanel;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiEventTriggerBlockPanel extends GuiDataTriggerBlockPanel<EventTriggerBlock> {
   public GuiEventTriggerBlockPanel(class_310 mc, GuiTriggerOverlayPanel overlay, EventTriggerBlock block) {
      super(mc, overlay, block);
      this.addPicker();
      this.addData();
      this.addDelay();
   }

   protected IKey getLabel() {
      return IKey.lang("mappet.gui.overlays.event");
   }

   protected ContentType getType() {
      return ContentType.EVENT;
   }
}
