package mchorse.mappet.api.schematics;

import java.io.File;
import java.io.IOException;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.manager.BaseManager;
import mchorse.mappet.api.utils.manager.ManagerCache;
import net.minecraft.class_2487;
import net.minecraft.class_2507;

public class SchematicManager extends BaseManager<Schematic> {
   public SchematicManager(File folder) {
      super(folder);
   }

   protected Schematic createData(String id, class_2487 tag) {
      Schematic schematic = new Schematic(0, 0, 0);
      schematic.deserializeNBT(tag);
      return schematic;
   }

   public boolean save(String id, Schematic data) {
      try {
         class_2507.method_10630(data.serializeNBT(), this.getFile(id));
         return true;
      } catch (IOException e) {
         e.printStackTrace();
         return false;
      }
   }

   protected class_2487 getCached(String id) throws Exception {
      class_2487 tag = null;
      File file = this.getFile(id);
      boolean isCaching = (Boolean)Mappet.generalDataCaching.get();
      long lastUpdated = file.lastModified();
      if (isCaching) {
         ManagerCache cache = (ManagerCache)this.cache.get(id);
         if (cache != null) {
            if (cache.lastUpdated < lastUpdated) {
               this.cache.remove(id);
            } else {
               tag = cache.tag;
               cache.update();
            }

            this.doExpirationCheck();
         }
      }

      if (tag == null) {
         tag = class_2507.method_10633(file);
         if (isCaching) {
            this.cache.put(id, new ManagerCache(tag, lastUpdated));
         }
      }

      return tag;
   }

   protected String getExtension() {
      return ".schematic";
   }
}
