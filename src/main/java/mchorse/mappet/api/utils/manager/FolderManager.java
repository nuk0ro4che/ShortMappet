package mchorse.mappet.api.utils.manager;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.AbstractData;

public abstract class FolderManager<T extends AbstractData> implements IManager<T> {
   protected Map<String, ManagerCache> cache = new HashMap();
   protected File folder;
   protected long lastCheck;

   public FolderManager(File folder) {
      if (folder != null) {
         this.folder = folder;
         this.folder.mkdirs();
      }

   }

   protected void doExpirationCheck() {
      int threshold = 30000;
      long current = System.currentTimeMillis();
      if (current - this.lastCheck > 30000L) {
         this.cache.values().removeIf((cache) -> current - cache.lastUsed > 30000L);
         this.lastCheck = current;
      }

   }

   public boolean exists(String name) {
      return this.getFile(name).exists();
   }

   public boolean rename(String id, String newId) {
      File file = this.getFile(id);
      File target = this.getFile(newId);
      if (target != null && target.getParentFile() != null) {
         target.getParentFile().mkdirs();
      }
      if (file != null && file.exists() && target != null && file.renameTo(target)) {
         if ((Boolean)Mappet.generalDataCaching.get()) {
            this.cache.put(newId, (ManagerCache)this.cache.remove(id));
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean delete(String name) {
      File file = this.getFile(name);
      if (file != null && file.delete()) {
         this.cache.remove(name);
         return true;
      } else {
         return false;
      }
   }

   public Collection<String> getKeys() {
      Set<String> set = new HashSet();
      if (this.folder == null) {
         return set;
      } else {
         this.recursiveFind(set, this.folder, "");
         return set;
      }
   }

   protected void recursiveFind(Set<String> set, File folder, String prefix) {
      for(File file : folder.listFiles()) {
         String name = file.getName();
         if (file.isFile() && name.endsWith(".json")) {
            set.add(prefix + name.replace(".json", ""));
         } else if (file.isDirectory()) {
            if (file.listFiles().length > 0) {
               this.recursiveFind(set, file, prefix + name + "/");
            } else {
               set.add(prefix + name + "/");
            }
         }
      }

   }

   protected boolean isData(File file) {
      return file.getName().endsWith(this.getExtension());
   }

   public File getFile(String name) {
      return this.folder == null ? null : new File(this.folder, name + this.getExtension());
   }

   public File getFolder() {
      return this.folder;
   }

   protected String getExtension() {
      return ".json";
   }
}
