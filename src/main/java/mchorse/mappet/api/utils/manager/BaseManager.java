package mchorse.mappet.api.utils.manager;

import java.io.File;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.utils.NBTToJsonLike;
import net.minecraft.class_2487;

public abstract class BaseManager<T extends AbstractData> extends FolderManager<T> {
   public BaseManager(File folder) {
      super(folder);
   }

   public final T create(String id, class_2487 tag) {
      T data = this.createData(id, tag);
      data.setId(id);
      return data;
   }

   protected abstract T createData(String var1, class_2487 var2);

   public T load(String id) {
      try {
         class_2487 tag = this.getCached(id);
         T data = this.create(id, tag);
         return data;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
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
         tag = NBTToJsonLike.read(file);
         if (isCaching) {
            this.cache.put(id, new ManagerCache(tag, lastUpdated));
         }
      }

      return tag;
   }

   public boolean save(String id, T data) {
      return this.save(id, (class_2487)data.serializeNBT());
   }

   public boolean save(String name, class_2487 tag) {
      try {
         NBTToJsonLike.write(this.getFile(name), tag);
         this.cache.remove(name);
         return true;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }
}
