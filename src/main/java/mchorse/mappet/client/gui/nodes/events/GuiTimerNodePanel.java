package mchorse.mappet.client.gui.nodes.events;

import mchorse.mappet.api.events.nodes.TimerNode;
import mchorse.mappet.client.gui.nodes.GuiEventBaseNodePanel;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiTimerNodePanel extends GuiEventBaseNodePanel<TimerNode> {
   public GuiTrackpadElement timer;

   public GuiTimerNodePanel(class_310 mc) {
      super(mc);
      this.timer = new GuiTrackpadElement(mc, (value) -> (this.node).timer = value.intValue());
      this.timer.integer().limit((double)0.0F);
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.nodes.event.timer")).marginTop(12), this.timer});
   }

   public void set(TimerNode node) {
      super.set(node);
      this.timer.setValue((double)node.timer);
   }
}
