package mchorse.mappet.client.gui.conditionModel;

import mchorse.mappet.tile.TileConditionModel;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiConditionModelBasicSettingsElement extends GuiElement {
   public GuiTrackpadElement frequency;
   public GuiToggleElement isGlobal;
   public GuiToggleElement isShadow;
   public TileConditionModel tile;

   public GuiConditionModelBasicSettingsElement(class_310 mc) {
      super(mc);
      this.isGlobal = new GuiToggleElement(mc, IKey.lang("mappet.gui.conditionModel.global"), (b) -> this.tile.isGlobal = b.isToggled());
      this.isShadow = new GuiToggleElement(mc, IKey.lang("mappet.gui.conditionModel.shadow"), (b) -> this.tile.isShadow = b.isToggled());
      this.frequency = (new GuiTrackpadElement(mc, (value) -> this.tile.frequency = value.intValue())).limit((double)1.0F).integer();
      this.add(new IGuiElement[]{this.isGlobal, this.isShadow});
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.conditionModel.frequency")).marginTop(6), this.frequency});
      this.flex().column(5).vertical().stretch();
   }

   public void set(TileConditionModel tile) {
      this.tile = tile;
      if (tile != null) {
         this.isGlobal.toggled(tile.isGlobal);
         this.isShadow.toggled(tile.isShadow);
         this.frequency.setValue((double)tile.frequency);
      }

   }
}
