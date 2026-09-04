package mchorse.mappet.api.dialogues.nodes;

import mchorse.mappet.api.dialogues.DialogueFragment;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public abstract class DialogueNode extends EventBaseNode {
   public DialogueFragment message = new DialogueFragment();

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return this.message.getProcessedText();
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      class_2487 message = this.message.serializeNBT();
      if (message.method_10546() > 0) {
         tag.method_10566("Message", message);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10573("Message", 10)) {
         this.message.deserializeNBT(tag.method_10562("Message"));
      }

   }
}
