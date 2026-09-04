package mchorse.mappet.client.gui.npc;

import mchorse.mappet.api.npcs.NpcState;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.class_310;

public abstract class GuiNpcPanel extends GuiElement {
   protected NpcState state;

   public GuiNpcPanel(class_310 mc) {
      super(mc);
      this.flex().column(5).vertical().stretch();
   }

   public void set(NpcState state) {
      this.state = state;
   }
}
