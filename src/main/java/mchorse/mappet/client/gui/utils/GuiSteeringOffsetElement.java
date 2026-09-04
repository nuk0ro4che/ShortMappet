package mchorse.mappet.client.gui.utils;

import java.util.function.Consumer;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import net.minecraft.class_2338;
import net.minecraft.class_310;

public class GuiSteeringOffsetElement extends GuiElement {
   GuiBlockPosElement position;

   public GuiSteeringOffsetElement(class_310 mc) {
      super(mc);
      this.position = new GuiBlockPosElement(mc, (Consumer)null);
      this.flex().column(5).stretch().vertical();
      this.add(this.position);
   }

   public void set(class_2338 pos) {
      this.position.set(pos);
   }
}
