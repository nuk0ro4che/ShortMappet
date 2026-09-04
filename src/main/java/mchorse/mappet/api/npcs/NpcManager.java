package mchorse.mappet.api.npcs;

import java.io.File;
import mchorse.mappet.api.utils.manager.BaseManager;
import net.minecraft.class_2487;

public class NpcManager extends BaseManager<Npc> {
   public NpcManager(File folder) {
      super(folder);
   }

   protected Npc createData(String id, class_2487 tag) {
      Npc npc = new Npc();
      if (tag != null) {
         npc.deserializeNBT(tag);
      } else {
         npc.states.put("default", new NpcState());
      }

      return npc;
   }
}
