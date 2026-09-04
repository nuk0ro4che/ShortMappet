package mchorse.mappet.client.gui.triggers.panels;

import mchorse.mappet.api.triggers.blocks.SoundTriggerBlock;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.client.gui.triggers.GuiTriggerOverlayPanel;
import mchorse.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mappet.client.gui.utils.overlays.GuiResourceLocationOverlayPanel;
import mchorse.mappet.client.gui.utils.overlays.GuiSoundOverlayPanel;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiCirculateElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_2960;
import net.minecraft.class_310;

public class GuiSoundTriggerBlockPanel extends GuiStringTriggerBlockPanel<SoundTriggerBlock> {
   public GuiCirculateElement target;

   public GuiSoundTriggerBlockPanel(class_310 mc, GuiTriggerOverlayPanel overlay, SoundTriggerBlock block) {
      super(mc, overlay, block);
      this.target = GuiMappetUtils.createTargetCirculate(mc, block.target, (targetx) -> (this.block).target = targetx);

      for(TargetMode target : TargetMode.values()) {
         if (target != TargetMode.PLAYER && target != TargetMode.GLOBAL) {
            this.target.disable(target.ordinal());
         }
      }

      this.addPicker();
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.conditions.target")).marginTop(12), this.target});
      this.addDelay();
   }

   protected IKey getLabel() {
      return IKey.lang("mappet.gui.overlays.sounds.main");
   }

   protected ContentType getType() {
      return null;
   }

   protected void openOverlay() {
      GuiResourceLocationOverlayPanel overlay = (new GuiSoundOverlayPanel(this.mc, this::setSound)).set((this.block).string);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.5F, 0.9F);
   }

   private void setSound(class_2960 location) {
      (this.block).string = location == null ? "" : location.toString();
   }
}
