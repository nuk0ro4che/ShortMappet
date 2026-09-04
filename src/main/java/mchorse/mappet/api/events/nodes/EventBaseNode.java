package mchorse.mappet.api.events.nodes;

import mchorse.mappet.api.events.EventContext;
import mchorse.mappet.api.utils.nodes.Node;
import net.minecraft.class_2487;

public abstract class EventBaseNode extends Node {
   public static final int HALT = -1;
   public static final int ALL = 0;
   public boolean binary;

   public abstract int execute(EventContext var1);

   protected int booleanToExecutionCode(boolean result) {
      if (this.binary) {
         return result ? 1 : 2;
      } else {
         return result ? 0 : -1;
      }
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      if (this.binary) {
         tag.method_10556("Binary", this.binary);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Binary")) {
         this.binary = tag.method_10577("Binary");
      }

   }
}
