package mchorse.mappet.client.gui.panels;

import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import net.minecraft.class_310;
import net.minecraft.class_746;

public abstract class GuiMappetRunPanel<T extends AbstractData> extends GuiMappetDashboardPanel<T> {
   public GuiIconElement run;

   public GuiMappetRunPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.run = new GuiIconElement(mc, Icons.PLAY, (b) -> this.run(this.mc.field_1724));
      this.run.tooltip(IKey.lang(this.getTitle() + "_run"), Direction.LEFT);
      this.iconBar.add(this.run);
   }

   protected abstract void run(class_746 var1);

   public void fill(T data, boolean allowed) {
      super.fill(data, allowed);
      this.run.setVisible(data != null);
   }
}
