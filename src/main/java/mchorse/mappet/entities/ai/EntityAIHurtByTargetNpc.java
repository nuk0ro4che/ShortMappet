package mchorse.mappet.entities.ai;

import net.minecraft.class_1314;
import net.minecraft.class_1399;

public class EntityAIHurtByTargetNpc extends class_1399 {
   public boolean reset;

   public EntityAIHurtByTargetNpc(class_1314 creatureIn, boolean entityCallsForHelpIn, Class<?>... excludedReinforcementTypes) {
      super(creatureIn, excludedReinforcementTypes);
      if (entityCallsForHelpIn) {
         this.method_6318(excludedReinforcementTypes);
      }

   }

   public boolean method_6266() {
      if (this.reset) {
         this.reset = false;
         return false;
      } else {
         return super.method_6266();
      }
   }
}
