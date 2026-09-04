package mchorse.mappet.entities.ai.fly;

import mchorse.mappet.entities.EntityNpc;
import net.minecraft.class_1335;
import net.minecraft.class_243;
import net.minecraft.class_5134;

public class FlyingMoveHelper extends class_1335 {
   private final EntityNpc npc;
   private boolean updating;

   public FlyingMoveHelper(EntityNpc entity) {
      super(entity);
      this.npc = entity;
   }

   public void method_6240() {
      if (this.updating) {
         this.updating = false;
         double movementSpeed = this.field_6372 * this.npc.method_26825(class_5134.field_23719);
         double verticalSpeed = this.field_6372 * (double)(Float)this.npc.getState().speed.get() / (double)8.0F;
         double dx = this.field_6370 - this.npc.method_23317();
         double dy = this.field_6369 - this.npc.method_23318();
         double dz = this.field_6367 - this.npc.method_23321();
         double squared = dx * dx + dy * dy + dz * dz;
         double distance = Math.sqrt(squared);
         movementSpeed = Math.min(distance / (double)5.0F, movementSpeed);
         if (this.npc.field_6235 == 0 && squared > (double)0.5F && distance > (double)0.0F) {
            class_243 old = this.npc.method_18798();
            double vx = old.field_1352 + (movementSpeed * dx / distance - old.field_1352) * movementSpeed;
            double vz = old.field_1350 + (movementSpeed * dz / distance - old.field_1350) * movementSpeed;
            double vy = verticalSpeed * dy / distance;
            if (vy > (double)0.0F) {
               vy += 0.1;
            }

            this.npc.method_18800(vx, vy, vz);
            this.npc.field_6007 = true;
         }

         this.npc.method_36456(this.method_6238(this.npc.method_36454(), (float)((Math.atan2(-dx, -dz) + Math.PI) * -57.29577951308232), 20.0F));
      }
   }

   public void method_6239(double x, double y, double z, double speed) {
      this.field_6370 = x;
      this.field_6369 = y;
      this.field_6367 = z;
      this.field_6372 = speed;
      this.updating = true;
   }

   public boolean method_6241() {
      return this.updating;
   }

   public double method_6242() {
      return this.field_6372;
   }
}
