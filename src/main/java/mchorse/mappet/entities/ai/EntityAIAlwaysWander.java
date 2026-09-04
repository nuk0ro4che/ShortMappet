package mchorse.mappet.entities.ai;

import net.minecraft.class_1314;
import net.minecraft.class_1394;
import net.minecraft.class_243;

public class EntityAIAlwaysWander extends class_1394 {
   public EntityAIAlwaysWander(class_1314 creature, double speed) {
      super(creature, speed);
   }

   public boolean method_6264() {
      return true;
   }

   public boolean method_6266() {
      return true;
   }

   public void method_6268() {
      super.method_6268();
      if (this.field_6566.method_5942().method_6357()) {
         class_243 position = this.method_6302();
         if (position != null) {
            this.field_6566.method_5942().method_6337(position.field_1352, position.field_1351, position.field_1350, this.field_6567);
         }
      }

   }
}
