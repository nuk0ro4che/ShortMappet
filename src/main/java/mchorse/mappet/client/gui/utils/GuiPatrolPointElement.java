package mchorse.mappet.client.gui.utils;

import java.util.function.Consumer;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.client.gui.triggers.GuiTriggerElement;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import net.minecraft.class_2338;
import net.minecraft.class_310;

public class GuiPatrolPointElement extends GuiElement {
   GuiBlockPosElement position;
   GuiTriggerElement trigger;

   public GuiPatrolPointElement(class_310 mc) {
      super(mc);
      this.position = new GuiBlockPosElement(mc, (Consumer)null);
      this.trigger = new GuiTriggerElement(mc);
      this.flex().column(5).stretch().vertical();
      this.add(new IGuiElement[]{this.position, this.trigger});
   }

   public void set(class_2338 pos, Trigger trigger) {
      this.position.set(pos);
      this.trigger.set(trigger);
   }
}
