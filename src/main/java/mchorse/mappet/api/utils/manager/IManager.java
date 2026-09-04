package mchorse.mappet.api.utils.manager;

import java.io.File;
import java.util.Collection;
import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_2487;

public interface IManager<T extends INBTSerializable<class_2487>> {
   boolean exists(String var1);

   default T create(String id) {
      return (T)this.create(id, (class_2487)null);
   }

   T create(String var1, class_2487 var2);

   T load(String var1);

   boolean save(String var1, class_2487 var2);

   boolean rename(String var1, String var2);

   boolean delete(String var1);

   File getFolder();

   Collection<String> getKeys();
}
