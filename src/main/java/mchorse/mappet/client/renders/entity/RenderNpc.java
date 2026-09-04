package mchorse.mappet.client.renders.entity;

import mchorse.mappet.entities.EntityNpc;
import mchorse.metamorph.client.render.MorphRenderPipeline;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_5617;
import net.minecraft.class_897;

public class RenderNpc extends class_897<EntityNpc> {
   private static final class_2960 EMPTY = new class_2960("mappet", "textures/entity/npc.png");

   public RenderNpc(class_5617.class_5618 context) {
      super(context);
      this.field_4673 = 0.6F;
   }

   public void method_3936(EntityNpc entity, float yaw, float tickDelta, class_4587 matrices, class_4597 consumers, int light) {
      this.field_4673 = (Float)entity.getState().shadowSize.get();
      float headYaw = entity.method_5791();
      float previousHeadYaw = entity.field_6259;
      float bodyYaw = entity.field_6283;
      float previousBodyYaw = entity.field_6220;
      entity.method_5847(entity.smoothYawHead);
      entity.field_6259 = entity.prevSmoothYawHead;
      entity.field_6283 = entity.smoothBodyYawHead;
      entity.field_6220 = entity.prevSmoothBodyYawHead;
      MorphRenderPipeline.drawEntity(entity.getMorph(), entity, matrices, consumers, light, tickDelta);
      entity.method_5847(headYaw);
      entity.field_6259 = previousHeadYaw;
      entity.field_6283 = bodyYaw;
      entity.field_6220 = previousBodyYaw;
      super.method_3936(entity, yaw, tickDelta, matrices, consumers, light);
   }

   public class_2960 method_3931(EntityNpc entity) {
      return EMPTY;
   }
}
