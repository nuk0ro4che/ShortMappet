package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.conditions.Condition;
import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_2487;

public class ConditionConditionBlock extends AbstractConditionBlock {
   public Condition condition = new Condition(false);

   public boolean evaluateBlock(DataContext context) {
      return this.condition.execute(context);
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      return class_1074.method_4662("mappet.gui.conditions.condition.string", new Object[]{this.condition.blocks.size()});
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10566("Condition", this.condition.serializeNBT());
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.condition.deserializeNBT(tag.method_10562("Condition"));
   }
}
