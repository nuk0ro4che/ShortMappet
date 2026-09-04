package mchorse.mappet.api.scripts.code.entities.ai;

import java.util.EnumSet;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.class_11;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1352;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_7;
import net.minecraft.class_1352.class_4134;
import org.apache.commons.lang3.ArrayUtils;

public class EntitiesAIPatrol extends class_1352 {
   private final class_1308 target;
   private final double speed;
   private int timer;
   private float prevWaterFactor;
   private int index;
   private int direction = 1;
   private class_2338[] patrolPoints;
   private boolean[] shouldCirculate;
   private String[] executeCommandOnArrival;

   public EntitiesAIPatrol(class_1308 target, double speed, class_2338[] patrolPoints, boolean[] shouldCirculate, String[] executeCommandOnArrival) {
      this.target = target;
      this.speed = speed;
      this.patrolPoints = patrolPoints;
      this.shouldCirculate = shouldCirculate;
      this.executeCommandOnArrival = executeCommandOnArrival;
      this.method_6265(EnumSet.of(class_4134.field_18405, class_4134.field_18406));
   }

   public boolean method_6264() {
      class_1309 target = this.target.method_5968();
      return target == null && this.patrolPoints.length > 0;
   }

   public boolean method_6266() {
      class_1309 target = this.target.method_5968();
      return target == null && this.patrolPoints.length > 0;
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
      if (this.index >= 0 && this.index < this.patrolPoints.length) {
         class_2338 pos = this.patrolPoints[this.index];
         if (this.target.method_5707(class_243.method_24953(pos)) < (double)2.0F) {
            int next = this.index + this.direction;
            if (this.shouldCirculate[this.index]) {
               this.index = MathUtils.cycler(this.index + this.direction, 0, this.patrolPoints.length - 1);
            } else {
               if (next < 0 || next >= this.patrolPoints.length) {
                  this.direction *= -1;
               }

               this.index += this.direction;
            }

            if (this.executeCommandOnArrival[this.index] != null) {
               this.target.method_5682().method_3734().method_44252(this.target.method_5671(), this.executeCommandOnArrival[this.index]);
            }

            this.timer = 0;
         }

         int x = pos.method_10263();
         int y = pos.method_10264();
         int z = pos.method_10260();
         this.target.method_5988().method_6230((double)x, (double)((float)y + this.target.method_17682()), (double)z, 10.0F, (float)this.target.method_5978());
         if (--this.timer <= 0) {
            this.timer = 10;
            class_11 path = this.target.method_5942().method_6352((double)x, (double)y, (double)z, 0);
            if (path != null) {
               this.target.method_5942().method_6334(path, this.speed);
            }
         }

      }
   }

   public class_2338[] getPatrolPoints() {
      return this.patrolPoints;
   }

   public boolean[] getShouldCirculate() {
      return this.shouldCirculate;
   }

   public String[] getExecuteCommandOnArrival() {
      return this.executeCommandOnArrival;
   }

   public void addPatrolPoint(class_2338 point, boolean shouldCirculate, String executeCommandOnArrival) {
      this.patrolPoints = (class_2338[])ArrayUtils.add(this.patrolPoints, point);
      this.shouldCirculate = ArrayUtils.add(this.shouldCirculate, shouldCirculate);
      this.executeCommandOnArrival = (String[])ArrayUtils.add(this.executeCommandOnArrival, executeCommandOnArrival);
   }
}
