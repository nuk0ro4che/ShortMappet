package mchorse.mappet.entities.ai.fly;

import java.util.EnumSet;
import java.util.Random;
import mchorse.mappet.entities.EntityNpc;
import net.minecraft.class_1314;
import net.minecraft.class_1352;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_1352.class_4134;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class EntityAINpcFly extends class_1352 {
   private class_1314 creature;
   private double speed;
   private Random rand;
   private double maxHeight;
   private double minHeight;
   private int stuckTimer;
   private int timeUntilNewDirection;
   private double targetX;
   private double targetY;
   private double targetZ;

   public EntityAINpcFly(class_1314 creature) {
      this.creature = creature;
      this.speed = (double)(Float)((EntityNpc)creature).getState().speed.get() / (double)2.0F;
      this.minHeight = (Double)((EntityNpc)creature).getState().flightMinHeight.get();
      this.maxHeight = (Double)((EntityNpc)creature).getState().flightMaxHeight.get();
      this.rand = new Random();
      this.method_6265(EnumSet.of(class_4134.field_18405));
   }

   public boolean method_6264() {
      return true;
   }

   public boolean method_6266() {
      return !this.creature.method_24828();
   }

   public void method_6268() {
      if (--this.timeUntilNewDirection <= 0 || this.creature.method_5942().method_6357()) {
         this.timeUntilNewDirection += this.rand.nextInt(30) + 5;
         this.targetX = this.creature.method_23317() + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
         class_239 rayTrace = this.creature.method_37908().method_17742(new class_3959(this.creature.method_19538(), new class_243(this.creature.method_23317(), (double)this.creature.method_37908().method_31607(), this.creature.method_23321()), class_3960.field_17558, class_242.field_1348, this.creature));
         double groundLevel = rayTrace != null && rayTrace.method_17784() != null ? rayTrace.method_17784().field_1351 : this.creature.method_23318();
         this.targetY = groundLevel + this.minHeight + (this.maxHeight - this.minHeight) * (double)this.rand.nextFloat();
         this.targetZ = this.creature.method_23321() + (double)((this.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
      }

      double dx = this.targetX - this.creature.method_23317();
      double dy = this.targetY - this.creature.method_23318();
      double dz = this.targetZ - this.creature.method_23321();
      double dist = dx * dx + dy * dy + dz * dz;
      if (!(dist < (double)2.0F) && !(dist > (double)256.0F) && this.stuckTimer <= 120) {
         ++this.stuckTimer;
      } else {
         this.stuckTimer = 0;
         this.timeUntilNewDirection = 0;
      }

      this.creature.method_5962().method_6239(this.targetX, this.targetY, this.targetZ, this.speed);
   }
}
