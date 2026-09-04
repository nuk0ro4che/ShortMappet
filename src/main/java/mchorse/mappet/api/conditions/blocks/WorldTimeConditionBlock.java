package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.utils.EnumUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_2487;

public class WorldTimeConditionBlock extends AbstractConditionBlock {
   public TimeCheck check;
   public int min;
   public int max;

   public WorldTimeConditionBlock() {
      this.check = WorldTimeConditionBlock.TimeCheck.DAY;
      this.max = 24000;
   }

   public boolean evaluateBlock(DataContext context) {
      long time = context.world.method_8532();
      if (this.check == WorldTimeConditionBlock.TimeCheck.DAY) {
         return time % 24000L < 12000L;
      } else if (this.check == WorldTimeConditionBlock.TimeCheck.NIGHT) {
         return time % 24000L >= 12000L;
      } else if (this.check != WorldTimeConditionBlock.TimeCheck.RANGE) {
         return false;
      } else {
         return time % 24000L >= (long)this.min && time % 24000L <= (long)this.max;
      }
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      String label = this.check.stringify();
      if (this.check == WorldTimeConditionBlock.TimeCheck.RANGE) {
         label = label + " " + this.min + "-" + this.max;
      }

      return label;
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10569("Check", this.check.ordinal());
      tag.method_10569("Min", this.min);
      tag.method_10569("Max", this.max);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.check = (TimeCheck)EnumUtils.getValue(tag.method_10550("Check"), WorldTimeConditionBlock.TimeCheck.values(), WorldTimeConditionBlock.TimeCheck.DAY);
      this.min = tag.method_10550("Min");
      this.max = tag.method_10550("Max");
   }

   public static enum TimeCheck {
      DAY,
      NIGHT,
      RANGE;

      @Environment(EnvType.CLIENT)
      public String stringify() {
         return class_1074.method_4662(this.getKey(), new Object[0]);
      }

      public String getKey() {
         return "mappet.gui.conditions.world_time.types." + this.name().toLowerCase();
      }
      private static TimeCheck[] $values() {
         return new TimeCheck[]{DAY, NIGHT, RANGE};
      }
   }
}
