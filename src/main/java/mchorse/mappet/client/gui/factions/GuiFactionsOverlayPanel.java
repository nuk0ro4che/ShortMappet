package mchorse.mappet.client.gui.factions;

import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiStringListElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiFactionsOverlayPanel extends GuiOverlayPanel {
   public GuiFactionsOverlayPanel(class_310 mc, List<String> keys, Consumer<String> callback) {
      super(mc, IKey.lang("mappet.gui.factions.relations.main"));
      GuiStringListElement list = new GuiStringListElement(mc, (Consumer)null);
      GuiButtonElement button = new GuiButtonElement(mc, IKey.lang("mappet.gui.factions.relations.add"), (b) -> {
         if (callback != null && !list.isDeselected()) {
            callback.accept((String)list.getCurrentFirst());
         }

         this.close();
      });
      list.add(keys);
      list.flex().relative(this.content).w(1.0F).h(1.0F, -35);
      button.flex().relative(this.content).x(1.0F).y(1.0F, -10).w(0.4F).anchor(1.0F, 1.0F);
      this.content.add(new IGuiElement[]{list, button});
   }
}
