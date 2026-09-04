package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.utils.Target;
import mchorse.mappet.api.utils.TargetMode;
import net.minecraft.class_2487;

public abstract class TargetConditionBlock extends AbstractConditionBlock {
   public String id = "";
   public Target target = new Target(this.getDefaultTarget());

   protected TargetMode getDefaultTarget() {
      return TargetMode.GLOBAL;
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("Id", this.id.trim());
      tag.method_10543(this.target.serializeNBT());
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.id = tag.method_10558("Id");
      this.target.deserializeNBT(tag);
   }
}
