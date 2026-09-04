package mchorse.mappet.api.utils;

import mchorse.mappet.compat.INBTSerializable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public abstract class AbstractBlock implements INBTSerializable<class_2487> {
   @Environment(EnvType.CLIENT)
   public abstract String stringify();

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      this.serializeNBT(tag);
      return tag;
   }

   protected abstract void serializeNBT(class_2487 var1);
}
