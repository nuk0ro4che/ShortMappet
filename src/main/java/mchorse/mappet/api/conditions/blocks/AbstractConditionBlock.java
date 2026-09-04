package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.utils.AbstractBlock;
import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_2487;

public abstract class AbstractConditionBlock extends AbstractBlock {
   public boolean not;
   public boolean or;

   public boolean evaluate(DataContext context) {
      boolean result = this.evaluateBlock(context);
      return this.not != result;
   }

   protected abstract boolean evaluateBlock(DataContext var1);

   protected void serializeNBT(class_2487 tag) {
      tag.method_10556("Not", this.not);
      tag.method_10556("Or", this.or);
   }

   public void deserializeNBT(class_2487 tag) {
      this.not = tag.method_10577("Not");
      this.or = tag.method_10577("Or");
   }

   public class_2487 toNBT() {
      class_2487 tag = new class_2487();
      this.serializeNBT(tag);
      return tag;
   }

   public String toString() {
      return "AbstractConditionBlock[type:" + CommonProxy.getConditionBlocks().getType(this) + "]";
   }
}
