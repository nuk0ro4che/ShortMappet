package mchorse.mappet.client.gui.npc;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.client.gui.states.GuiStatesOverlay;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlay;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiNpcMetaPanel extends GuiNpcPanel {
   public GuiTextElement id;
   public GuiButtonElement states;
   public GuiToggleElement unique;
   public GuiTrackpadElement pathDistance;

   public GuiNpcMetaPanel(class_310 mc, boolean id) {
      super(mc);
      this.id = new GuiTextElement(mc, 1000, (t) -> this.state.id.set(t));
      this.states = new GuiButtonElement(mc, IKey.lang("mappet.gui.npcs.meta.states.pick"), (b) -> this.openStates());
      this.unique = new GuiToggleElement(mc, IKey.lang("mappet.gui.npcs.meta.unique"), (b) -> this.state.unique.set(b.isToggled()));
      this.pathDistance = new GuiTrackpadElement(mc, (v) -> this.state.pathDistance.set(v.floatValue()));
      if (id) {
         this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.meta.id")), this.id});
      }

      this.add(new IGuiElement[]{this.states, this.unique});
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.npcs.meta.path_distance")), this.pathDistance});
   }

   private void openStates() {
      GuiStatesOverlay overlay = new GuiStatesOverlay(this.mc, IKey.lang("mappet.gui.npcs.meta.states.title"), this.state.states);
      GuiOverlay.addOverlay(GuiBase.getCurrent(), overlay, 0.6F, 0.8F);
   }

   public void set(NpcState state) {
      super.set(state);
      this.id.setText((String)state.id.get());
      this.unique.toggled((Boolean)state.unique.get());
      this.pathDistance.setValue((double)(Float)state.pathDistance.get());
   }
}
