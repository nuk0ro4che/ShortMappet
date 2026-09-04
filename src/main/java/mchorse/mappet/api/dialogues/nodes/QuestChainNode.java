package mchorse.mappet.api.dialogues.nodes;

import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.events.EventContext;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class QuestChainNode extends EventBaseNode {
   public String chain = "";
   public String subject = "";

   public int execute(EventContext context) {
      if (context instanceof DialogueContext) {
         ((DialogueContext)context).setQuestChain(this);
      }

      return -1;
   }

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return this.chain;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10582("Chain", this.chain);
      tag.method_10582("Subject", this.subject.trim());
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Chain")) {
         this.chain = tag.method_10558("Chain");
      }

      if (tag.method_10545("Subject")) {
         this.subject = tag.method_10558("Subject");
      }

   }
}
