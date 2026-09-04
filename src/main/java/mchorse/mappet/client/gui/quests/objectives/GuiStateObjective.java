package mchorse.mappet.client.gui.quests.objectives;

import mchorse.mappet.api.quests.objectives.StateObjective;
import mchorse.mappet.client.gui.conditions.GuiCheckerElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_310;

public class GuiStateObjective extends GuiObjective<StateObjective> {
   public GuiCheckerElement expression;

   public GuiStateObjective(class_310 mc, StateObjective objective) {
      super(mc, objective);
      this.expression = new GuiCheckerElement(mc, objective.expression);
      this.expression.flex().relative(this).y(12).w(1.0F);
      this.message.flex().relative(this).y(1.0F).w(1.0F).anchorY(1.0F);
      this.flex().h(69);
      this.add(new IGuiElement[]{this.expression, this.message});
   }

   public IKey getMessageTooltip() {
      return IKey.EMPTY;
   }

   public void draw(GuiContext context) {
      super.draw(context);
      GuiDraw.drawStringWithShadow(this.font, class_1074.method_4662("mappet.gui.quests.objective_state.expression", new Object[0]), this.expression.area.x, this.expression.area.y - 12, 16777215);
   }
}
