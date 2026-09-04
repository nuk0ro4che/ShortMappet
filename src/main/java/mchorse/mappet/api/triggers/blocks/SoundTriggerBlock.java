package mchorse.mappet.api.triggers.blocks;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.utils.EnumUtils;
import mchorse.mappet.utils.WorldUtils;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class SoundTriggerBlock extends StringTriggerBlock {
   public TargetMode target;

   public SoundTriggerBlock() {
      this.target = TargetMode.GLOBAL;
   }

   public SoundTriggerBlock(String string) {
      super(string);
      this.target = TargetMode.GLOBAL;
   }

   public void trigger(DataContext context) {
      if (!this.string.isEmpty()) {
         if (this.target == TargetMode.GLOBAL) {
            for(class_3222 player : context.server.method_3760().method_14571()) {
               WorldUtils.playSound(player, this.string);
            }
         } else {
            class_1657 player = context.getPlayer();
            if (player instanceof class_3222) {
               WorldUtils.playSound((class_3222)player, this.string);
            }
         }

      }
   }

   protected String getKey() {
      return "Sound";
   }

   protected void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10569("Target", this.target.ordinal());
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.target = (TargetMode)EnumUtils.getValue(tag.method_10550("Target"), TargetMode.values(), TargetMode.GLOBAL);
   }
}
