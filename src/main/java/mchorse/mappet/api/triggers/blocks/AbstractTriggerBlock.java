package mchorse.mappet.api.triggers.blocks;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.utils.AbstractBlock;
import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_2487;

public abstract class AbstractTriggerBlock extends AbstractBlock {
   public int frequency = 1;
   private int tick;

   @Environment(EnvType.CLIENT)
   public String stringify() {
      return class_1074.method_4662("mappet.gui.trigger_types." + CommonProxy.getTriggerBlocks().getType(this), new Object[0]);
   }

   public void triggerWithFrequency(DataContext context) {
      ++this.tick;
      if (this.tick > 0 && this.tick % Math.max(this.frequency, 1) == 0) {
         this.trigger(context);
         this.tick = 0;
      }

   }

   public abstract void trigger(DataContext var1);

   public abstract boolean isEmpty();

   protected void serializeNBT(class_2487 tag) {
      tag.method_10569("Frequency", this.frequency);
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Delay")) {
         tag.method_10566("Frequency", tag.method_10580("Delay"));
      }

      this.frequency = tag.method_10550("Frequency");
   }

   public class_2487 toNBT() {
      class_2487 tag = new class_2487();
      this.serializeNBT(tag);
      return tag;
   }
}
