package mchorse.mappet.client.gui.huds;

import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.api.huds.HUDMorph;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import net.minecraft.class_310;

public class GuiHUDMorphListElement extends GuiListElement<HUDMorph> {
   public GuiHUDMorphListElement(class_310 mc, Consumer<List<HUDMorph>> callback) {
      super(mc, callback);
   }

   protected String elementToString(HUDMorph element) {
      return element.morph.isEmpty() ? "-" : element.morph.get().getDisplayName();
   }
}
