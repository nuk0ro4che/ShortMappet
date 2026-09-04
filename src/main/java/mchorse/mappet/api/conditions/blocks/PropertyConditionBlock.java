package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.utils.Comparison;
import net.minecraft.class_2487;

public abstract class PropertyConditionBlock extends TargetConditionBlock {
   public Comparison comparison = new Comparison();

   protected boolean compare(double a) {
      return this.comparison.compare(a);
   }

   protected boolean compareString(String a) {
      return this.comparison.compareString(a);
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10543(this.comparison.serializeNBT());
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.comparison.deserializeNBT(tag);
   }
}
