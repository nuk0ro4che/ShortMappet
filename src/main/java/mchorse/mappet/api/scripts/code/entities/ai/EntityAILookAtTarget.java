package mchorse.mappet.api.scripts.code.entities.ai;

import java.util.EnumSet;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1352;
import net.minecraft.class_1352.class_4134;

public class EntityAILookAtTarget extends class_1352 {
   private final class_1308 entity;
   private final class_1297 target;
   private final float chance;
   private int lookTime;

   public EntityAILookAtTarget(class_1308 entity, class_1297 target, float chance) {
      this.entity = entity;
      this.target = target;
      this.chance = chance;
      this.method_6265(EnumSet.of(class_4134.field_18406));
   }

   public boolean method_6264() {
      return !(this.entity.method_6051().method_43057() >= this.chance);
   }

   public boolean method_6266() {
      if (!this.target.method_5805()) {
         return false;
      } else {
         return this.lookTime > 0;
      }
   }

   public void method_6269() {
      this.lookTime = 40 + this.entity.method_6051().method_43048(40);
   }

   public void method_6270() {
      this.lookTime = 0;
   }

   public void method_6268() {
      this.entity.method_5988().method_6230(this.target.method_23317(), this.target.method_23320(), this.target.method_23321(), (float)this.entity.method_20240(), (float)this.entity.method_5978());
      --this.lookTime;
   }

   public class_1297 getTarget() {
      return this.target;
   }
}
