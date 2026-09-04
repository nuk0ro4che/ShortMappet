package mchorse.mappet.api.events.nodes;

import mchorse.mappet.api.events.EventContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_2487;

public class TimerNode extends EventBaseNode {
   public int timer;

   public int execute(EventContext context) {
      if (this.timer > 0) {
         context.addExecutionFork(this, this.timer);
         return -1;
      } else {
         return 0;
      }
   }

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return class_1074.method_4662("mappet.gui.nodes.event.ticks", new Object[]{this.timer});
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10569("Timer", this.timer);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Timer")) {
         this.timer = tag.method_10550("Timer");
      }

   }
}
