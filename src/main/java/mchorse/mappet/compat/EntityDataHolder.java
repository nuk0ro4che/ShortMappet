package mchorse.mappet.compat;

import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_2940;
import net.minecraft.class_2943;
import net.minecraft.class_2945;

public interface EntityDataHolder {
   class_2940<Boolean> MAPPET_LAY = class_2945.method_12791(class_1297.class, class_2943.field_13323);
   class_2940<Boolean> MAPPET_SOLID_HITBOX = class_2945.method_12791(class_1297.class, class_2943.field_13323);

   class_2487 mappet$getPersistentData();

   default boolean mappet$isSolidHitbox() {
      if (this instanceof class_1297 entity) {
         class_2945 tracker = entity.method_5841();
         if (tracker.method_51696(MAPPET_SOLID_HITBOX)) {
            return tracker.method_12789(MAPPET_SOLID_HITBOX);
         }
      }
      return this.mappet$getPersistentData().method_10545("SolidHitbox") && this.mappet$getPersistentData().method_10577("SolidHitbox");
   }

   default void mappet$setSolidHitbox(boolean solid) {
      this.mappet$getPersistentData().method_10556("SolidHitbox", solid);
      if (this instanceof class_1297 entity) {
         class_2945 tracker = entity.method_5841();
         if (tracker.method_51696(MAPPET_SOLID_HITBOX)) {
            tracker.method_12778(MAPPET_SOLID_HITBOX, solid);
         }
      }
   }

   default boolean mappet$isLay() {
      if (!(this instanceof class_1297 entity) || !(entity instanceof class_1657 player)) {
         return false;
      }

      class_2945 tracker = player.method_5841();
      return tracker.method_51696(MAPPET_LAY) && tracker.method_12789(MAPPET_LAY);
   }

   default void mappet$setLay(boolean lay) {
      if (this instanceof class_1297 entity && entity instanceof class_1657 player) {
         class_2945 tracker = player.method_5841();
         if (tracker.method_51696(MAPPET_LAY)) {
            tracker.method_12778(MAPPET_LAY, lay);
         }
      }
   }

}
