package mchorse.mappet.compat;

import net.minecraft.class_1297;
import net.minecraft.class_2487;

public final class EntityData {
   private EntityData() {
   }

   public static class_2487 get(class_1297 entity) {
      return ((EntityDataHolder)entity).mappet$getPersistentData();
   }
}
