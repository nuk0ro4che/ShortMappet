package mchorse.mappet.api.huds;

import java.io.File;
import mchorse.mappet.api.utils.manager.BaseManager;
import net.minecraft.class_2487;

public class HUDManager extends BaseManager<HUDScene> {
   public HUDManager(File folder) {
      super(folder);
   }

   protected HUDScene createData(String id, class_2487 tag) {
      HUDScene scene = new HUDScene();
      if (tag != null) {
         scene.deserializeNBT(tag);
      }

      return scene;
   }
}
