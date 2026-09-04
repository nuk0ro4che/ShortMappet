package mchorse.mappet.entities.ai;

import net.minecraft.class_1314;
import net.minecraft.class_1366;

public class EntityAIAttackNpcMelee extends class_1366 {
   private final int delay;

   public EntityAIAttackNpcMelee(class_1314 creature, double speed, boolean useLongMemory, int delay) {
      super(creature, speed, useLongMemory);
      this.delay = Math.max(1, delay);
   }

   protected int method_28349() {
      return this.delay;
   }
}
