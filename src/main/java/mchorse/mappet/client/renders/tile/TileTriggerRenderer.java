package mchorse.mappet.client.renders.tile;

import mchorse.mappet.tile.TileTrigger;
import mchorse.mclib.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TileTriggerRenderer extends TileBaseBlockRenderer<TileTrigger> {
   public TileTriggerRenderer() {
      super(new Color(0.94F, 1.0F, 0.11F, 0.5F));
   }
}
