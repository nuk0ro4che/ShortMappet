package mchorse.mappet.api.conditions;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.conditions.blocks.AbstractConditionBlock;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class Condition implements INBTSerializable<class_2487> {
   public final List<AbstractConditionBlock> blocks = new ArrayList();
   private boolean defaultValue;

   public Condition(boolean defaultValue) {
      this.defaultValue = defaultValue;
   }

   public boolean execute(DataContext context) {
      if (this.blocks.isEmpty()) {
         return this.defaultValue;
      } else {
         boolean result = ((AbstractConditionBlock)this.blocks.get(0)).evaluate(context);

         for(int i = 1; i < this.blocks.size(); ++i) {
            AbstractConditionBlock block = (AbstractConditionBlock)this.blocks.get(i);
            boolean value = block.evaluate(context);
            if (block.or) {
               result = result || value;
            } else {
               result = result && value;
            }
         }

         return result;
      }
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 blocks = new class_2499();

      for(AbstractConditionBlock block : this.blocks) {
         class_2487 blockTag = block.serializeNBT();
         blockTag.method_10582("Type", CommonProxy.getConditionBlocks().getType(block));
         blocks.add(blockTag);
      }

      if (blocks.size() > 0) {
         tag.method_10566("Blocks", blocks);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      class_2499 blocks = tag.method_10554("Blocks", 10);
      this.blocks.clear();

      for(int i = 0; i < blocks.size(); ++i) {
         class_2487 blockTag = blocks.method_10602(i);
         AbstractConditionBlock block = (AbstractConditionBlock)CommonProxy.getConditionBlocks().create(blockTag.method_10558("Type"));
         if (block != null) {
            block.deserializeNBT(blockTag);
            this.blocks.add(block);
         }
      }

   }

   public String toString() {
      String result = "mappet.condition[";

      for(AbstractConditionBlock block : this.blocks) {
         result = result + block.toString() + ",";
      }

      result = result + "]";
      return result;
   }
}
