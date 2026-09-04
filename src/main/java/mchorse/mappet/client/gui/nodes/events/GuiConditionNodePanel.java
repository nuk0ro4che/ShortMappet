package mchorse.mappet.client.gui.nodes.events;

import mchorse.mappet.api.events.nodes.ConditionNode;
import mchorse.mappet.client.gui.conditions.GuiCheckerElement;
import mchorse.mappet.client.gui.nodes.GuiEventBaseNodePanel;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiConditionNodePanel extends GuiEventBaseNodePanel<ConditionNode> {
   public GuiCheckerElement checker;

   public GuiConditionNodePanel(class_310 mc) {
      super(mc);
      this.checker = new GuiCheckerElement(mc);
      this.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.nodes.event.condition")).marginTop(12), this.checker, this.binary});
   }

   public void set(ConditionNode node) {
      super.set(node);
      this.checker.set(node.condition);
   }
}
