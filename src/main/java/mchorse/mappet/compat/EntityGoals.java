package mchorse.mappet.compat;

import mchorse.mappet.mixins.MobEntityAccessor;
import net.minecraft.class_1308;
import net.minecraft.class_1355;

public final class EntityGoals {
   private EntityGoals() {
   }

   public static class_1355 goals(class_1308 entity) {
      return ((MobEntityAccessor)entity).mappet$getGoalSelector();
   }

   public static class_1355 targets(class_1308 entity) {
      return ((MobEntityAccessor)entity).mappet$getTargetSelector();
   }
}
