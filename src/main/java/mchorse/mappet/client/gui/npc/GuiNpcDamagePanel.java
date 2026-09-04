package mchorse.mappet.client.gui.npc;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiNpcDamagePanel extends GuiNpcPanel {
   public GuiTrackpadElement damage;
   public GuiTrackpadElement damageDelay;
   public GuiTrackpadElement fallback;
   public GuiToggleElement canFallDamage;
   public GuiToggleElement canGetBurned;
   public GuiToggleElement invincible;
   public GuiToggleElement killable;

   public GuiNpcDamagePanel(class_310 mc) {
      super(mc);
      this.damage = new GuiTrackpadElement(mc, (v) -> this.state.damage.set(v.floatValue()));
      this.damage.limit((double)0.0F);
      this.damageDelay = new GuiTrackpadElement(mc, (v) -> this.state.damageDelay.set(v.intValue()));
      this.damageDelay.limit((double)0.0F, (double)200.0F);
      this.fallback = new GuiTrackpadElement(mc, (v) -> this.state.fallback.set(v.floatValue()));
      this.fallback.limit((double)0.0F, (double)64.0F);
      this.canFallDamage = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.damage.fall"), (b) -> this.state.canFallDamage.set(b.isToggled()));
      this.canGetBurned = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.damage.fire"), (b) -> this.state.canGetBurned.set(b.isToggled()));
      this.invincible = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.damage.invincible"), (b) -> this.state.invincible.set(b.isToggled()));
      this.killable = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.damage.killable"), (b) -> this.state.killable.set(b.isToggled()));
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.damage.damage")), this.damage});
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.damage.damage_delay")), this.damageDelay});
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.damage.fallback")), this.fallback});
      this.add(new IGuiElement[]{this.canFallDamage.marginTop(12), this.canGetBurned, this.invincible, this.killable});
   }

   public void set(NpcState state) {
      super.set(state);
      this.damageDelay.setValue((double)(Integer)state.damageDelay.get());
      this.damage.setValue((double)(Float)state.damage.get());
      this.canFallDamage.toggled((Boolean)state.canFallDamage.get());
      this.canGetBurned.toggled((Boolean)state.canGetBurned.get());
      this.invincible.toggled((Boolean)state.invincible.get());
      this.killable.toggled((Boolean)state.killable.get());
      this.fallback.setValue((double)(Float)state.fallback.get());
   }
}
