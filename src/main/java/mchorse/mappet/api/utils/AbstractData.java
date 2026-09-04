package mchorse.mappet.api.utils;

import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_2487;

public abstract class AbstractData implements INBTSerializable<class_2487>, IID {
   private String id;

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }
}
