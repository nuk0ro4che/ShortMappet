package mchorse.mappet.api.dialogues;

import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.factory.IFactory;
import mchorse.mappet.api.utils.nodes.NodeSystem;
import net.minecraft.class_2487;

public class Dialogue extends NodeSystem<EventBaseNode> {
   public boolean closable = true;
   public Trigger onClose = new Trigger();

   public Dialogue(IFactory<EventBaseNode> factory) {
      super(factory);
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10556("Closable", this.closable);
      tag.method_10566("OnClose", this.onClose.serializeNBT());
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Closable")) {
         this.closable = tag.method_10577("Closable");
      }

      if (tag.method_10545("OnClose")) {
         this.onClose.deserializeNBT(tag.method_10562("OnClose"));
      }

   }
}
