package mchorse.mappet.api.scripts.code.entities.ai.rotations;

import java.util.EnumSet;
import net.minecraft.class_1308;
import net.minecraft.class_1352;
import net.minecraft.class_1352.class_4134;

public class EntityAIRotations extends class_1352 {
   private final class_1308 entity;
   private final float yaw;
   private final float pitch;
   private final float yawHead;
   private final float chance;

   public EntityAIRotations(class_1308 entity, float yaw, float pitch, float yawHead, float chance) {
      this.entity = entity;
      this.yaw = yaw;
      this.pitch = pitch;
      this.chance = chance;
      this.yawHead = yawHead;
      this.method_6265(EnumSet.of(class_4134.field_18406));
   }

   public boolean method_6264() {
      return this.entity.method_6051().method_43057() < this.chance;
   }

   public boolean method_6266() {
      return false;
   }

   public void method_6269() {
      this.entity.method_36456(this.yaw);
      this.entity.method_36457(this.pitch);
      this.entity.method_5847(this.yawHead);
   }
}
