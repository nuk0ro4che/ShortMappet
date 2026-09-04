package mchorse.mappet.api.utils.manager;

import net.minecraft.class_2487;

public class ManagerCache {
   public class_2487 tag;
   public long lastUpdated;
   public long lastUsed;

   public ManagerCache(class_2487 tag, long lastUpdated) {
      this.tag = tag;
      this.lastUpdated = lastUpdated;
      this.update();
   }

   public void update() {
      this.lastUsed = System.currentTimeMillis();
   }
}
