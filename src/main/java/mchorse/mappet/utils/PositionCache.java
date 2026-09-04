package mchorse.mappet.utils;

import net.minecraft.class_1657;
import net.minecraft.class_243;

public class PositionCache {
   public class_243 lastPosition;
   public class_243 lastLastPosition;
   private int timer;

   public void setLastPosition(class_1657 player) {
      this.lastLastPosition = this.lastPosition;
      this.lastPosition = new class_243(player.method_23317(), player.method_23318(), player.method_23321());
   }

   public void resetLastPositionTimer() {
      this.timer = 0;
   }

   public boolean updatePlayer(class_1657 player) {
      ++this.timer;
      if (this.timer >= 10) {
         this.setLastPosition(player);
         this.timer = 0;
         return true;
      } else {
         return false;
      }
   }
}
