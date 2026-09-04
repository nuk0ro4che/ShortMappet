package mchorse.mappet.client.gui.quests.objectives;

import mchorse.mappet.api.quests.objectives.AbstractObjective;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_1074;
import net.minecraft.class_310;

public abstract class GuiObjective<T extends AbstractObjective> extends GuiElement {
   public GuiTextElement message;
   public T objective;

   public GuiObjective(class_310 mc, T objective) {
      super(mc);
      this.objective = objective;
      this.message = new GuiTextElement(mc, 1000, (t) -> this.objective.message = t);
      this.message.tooltip(IKey.comp(new IKey[]{IKey.lang("mappet.gui.quests.objectives.message_tooltip"), this.getMessageTooltip()}));
      this.message.setText(objective.message);
   }

   public abstract IKey getMessageTooltip();

   public void draw(GuiContext context) {
      super.draw(context);
      if (this.message.hasParent()) {
         GuiDraw.drawStringWithShadow(this.font, class_1074.method_4662("mappet.gui.quests.objectives.message", new Object[0]), this.message.area.x, this.message.area.y - 12, 16777215);
      }

   }
}
