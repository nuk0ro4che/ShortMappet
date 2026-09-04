package mchorse.mappet.entities.ai;

import java.util.EnumSet;
import mchorse.mappet.entities.EntityNpc;
import net.minecraft.class_1309;
import net.minecraft.class_1352;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_7;
import net.minecraft.class_1352.class_4134;

public class EntityAIFollowTarget extends class_1352 {
   private static final double TELEPORT_DISTANCE = (double)144.0F;
   private final EntityNpc target;
   private class_1309 follow;
   private final double speed;
   private int timer;
   private float max;
   private float min;
   private float prevWaterFactor;

   public EntityAIFollowTarget(EntityNpc target, double speed, float min, float max) {
      this.target = target;
      this.speed = speed;
      this.min = min;
      this.max = max;
      this.method_6265(EnumSet.of(class_4134.field_18405, class_4134.field_18406));
   }

   public boolean method_6264() {
      class_1309 target = this.target.getFollowTarget();
      if (target == null) {
         return false;
      } else if (target instanceof class_1657 && ((class_1657)target).method_7325()) {
         return false;
      } else if (this.target.method_5858(target) < (double)(this.min * this.min)) {
         return false;
      } else {
         this.follow = target;
         return true;
      }
   }

   public boolean method_6266() {
      return !this.target.method_5942().method_6357() && this.target.method_5858(this.follow) > (double)(this.min * this.min);
   }

   public void method_6269() {
      this.timer = 0;
      this.prevWaterFactor = this.target.method_5944(class_7.field_18);
      this.target.method_5941(class_7.field_18, 0.0F);
   }

   public void method_6270() {
      this.follow = null;
      this.target.method_5942().method_6340();
      this.target.method_5941(class_7.field_18, this.prevWaterFactor);
   }

   public void method_6268() {
      this.target.method_5988().method_6226(this.follow, 10.0F, (float)this.target.method_5978());
      if (this.timer > 0) {
         --this.timer;
      } else {
         this.timer = 10;
         if (!this.target.method_5942().method_6335(this.follow, this.speed) && !this.target.method_5934() && !this.target.method_5765() || this.target.method_5858(this.follow) >= (double)144.0F) {
            int x = class_3532.method_15357(this.follow.method_23317()) - 2;
            int z = class_3532.method_15357(this.follow.method_23321()) - 2;
            int y = class_3532.method_15357(this.follow.method_5829().field_1322);

            for(int bx = 0; bx <= 4; ++bx) {
               for(int bz = 0; bz <= 4; ++bz) {
                  if ((bx < 1 || bz < 1 || bx > 3 || bz > 3) && this.canTeleport(x, z, y, bx, bz)) {
                     this.target.method_5808((double)((float)(x + bx) + 0.5F), (double)y, (double)((float)(z + bz) + 0.5F), this.target.method_36454(), this.target.method_36455());
                     this.target.method_5942().method_6340();
                     return;
                  }
               }
            }
         }

      }
   }

   private boolean canTeleport(int x, int z, int y, int offsetX, int offsetY) {
      class_1937 world = this.target.method_37908();
      class_2338 pos = new class_2338(x + offsetX, y - 1, z + offsetY);
      class_2680 state = world.method_8320(pos);
      return state.method_26206(world, pos, class_2350.field_11036) && world.method_22347(pos.method_10084()) && world.method_22347(pos.method_10086(2));
   }
}
