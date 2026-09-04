package mchorse.mappet.client.gui.npc;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiNpcHealthPanel extends GuiNpcPanel {
   public GuiTrackpadElement maxHealth;
   public GuiTrackpadElement health;
   public GuiTrackpadElement regenDelay;
   public GuiTrackpadElement regenFrequency;

   public GuiNpcHealthPanel(class_310 mc) {
      super(mc);
      this.maxHealth = new GuiTrackpadElement(mc, (v) -> this.state.maxHealth.set(v.floatValue()));
      this.maxHealth.limit((double)0.0F);
      this.health = new GuiTrackpadElement(mc, (v) -> this.state.health.set(v.floatValue()));
      this.health.limit((double)0.0F);
      this.regenDelay = new GuiTrackpadElement(mc, (v) -> this.state.regenDelay.set(v.intValue()));
      this.regenDelay.limit((double)0.0F).integer();
      this.regenFrequency = new GuiTrackpadElement(mc, (v) -> this.state.regenFrequency.set(v.intValue()));
      this.regenFrequency.limit((double)1.0F).integer();
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.health.max_hp")), this.maxHealth});
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.health.hp")), this.health});
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.health.regen_delay")), this.regenDelay});
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.health.regen_frequency")), this.regenFrequency});
   }

   public void set(NpcState state) {
      super.set(state);
      this.maxHealth.setValue((double)(Float)state.maxHealth.get());
      this.health.setValue((double)(Float)state.health.get());
      this.regenDelay.setValue((double)(Integer)state.regenDelay.get());
      this.regenFrequency.setValue((double)(Integer)state.regenFrequency.get());
   }
}
