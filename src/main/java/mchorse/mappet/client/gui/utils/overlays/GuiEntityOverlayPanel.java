package mchorse.mappet.client.gui.utils.overlays;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import mchorse.mappet.api.quests.objectives.KillObjective;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_7923;

public class GuiEntityOverlayPanel extends GuiResourceLocationOverlayPanel {
   private static Set<class_2960> getKeys() {
      Set<class_2960> keys = new HashSet(class_7923.field_41177.method_10235());
      keys.add(KillObjective.PLAYER_ID);
      return keys;
   }

   public GuiEntityOverlayPanel(class_310 mc, Consumer<class_2960> callback) {
      super(mc, IKey.lang("mappet.gui.overlays.entities.main"), getKeys(), callback);
   }
}
