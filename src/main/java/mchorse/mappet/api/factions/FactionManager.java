package mchorse.mappet.api.factions;

import java.io.File;
import mchorse.mappet.api.utils.manager.BaseManager;
import net.minecraft.class_2487;

public class FactionManager extends BaseManager<Faction> {
   public FactionManager(File folder) {
      super(folder);
   }

   protected Faction createData(String id, class_2487 tag) {
      Faction faction = new Faction();
      if (tag != null) {
         faction.deserializeNBT(tag);
      }

      return faction;
   }
}
