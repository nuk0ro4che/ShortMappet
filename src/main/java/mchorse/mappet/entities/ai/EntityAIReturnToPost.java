package mchorse.mappet.entities.ai;

import java.util.EnumSet;
import mchorse.mappet.entities.EntityNpc;
import net.minecraft.class_1352;
import net.minecraft.class_1408;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_7;
import net.minecraft.class_1352.class_4134;

public class EntityAIReturnToPost extends class_1352 {
   private final EntityNpc target;
   private class_2338 post;
   private final double speed;
   private int timer;
   private float min;
   private float prevWaterFactor;

   public EntityAIReturnToPost(EntityNpc target, class_2338 post, double followSpeedIn, float minDistIn) {
      this.target = target;
      this.post = post;
      this.speed = followSpeedIn;
      this.min = minDistIn;
      this.method_6265(EnumSet.of(class_4134.field_18405, class_4134.field_18406));
   }

   public boolean method_6264() {
      if (this.target.method_5968() != null) {
         return false;
      } else {
         return this.target.method_5707(class_243.method_24953(this.post)) > (double)(this.min * this.min);
      }
   }

   public boolean method_6266() {
      if (this.target.method_5968() != null) {
         return false;
      } else {
         return !this.target.method_5942().method_6357() && this.target.method_5707(class_243.method_24953(this.post)) > (double)(this.min * this.min);
      }
   }

   public void method_6269() {
      this.timer = 0;
      this.prevWaterFactor = this.target.method_5944(class_7.field_18);
      this.target.method_5941(class_7.field_18, 0.0F);
   }

   public void method_6270() {
      this.target.method_5942().method_6340();
      this.target.method_5941(class_7.field_18, this.prevWaterFactor);
      this.target.method_36457(0.0F);
   }

   public void method_6268() {
      int x = this.post.method_10263();
      int y = this.post.method_10264();
      int z = this.post.method_10260();
      this.target.method_5988().method_6230((double)x, (double)((float)y + this.target.method_17682()), (double)z, 10.0F, (float)this.target.method_5978());
      if (this.timer > 0) {
         --this.timer;
      } else {
         class_1408 navigator = this.target.method_5942();
         navigator.method_6337((double)x, (double)y, (double)z, this.speed);
         this.timer = 10;
      }
   }
}
