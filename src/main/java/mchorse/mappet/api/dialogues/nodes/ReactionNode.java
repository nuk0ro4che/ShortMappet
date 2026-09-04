package mchorse.mappet.api.dialogues.nodes;

import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.events.EventContext;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_2487;

public class ReactionNode extends DialogueNode {
   public AbstractMorph morph;
   public String sound = "";
   public boolean read;
   public String marker = "";

   public ReactionNode() {
   }

   public ReactionNode(String message) {
      this.message.text = message;
   }

   public int execute(EventContext context) {
      if (context instanceof DialogueContext) {
         ((DialogueContext)context).reactionNode = this;
      }

      return 0;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      if (this.morph != null) {
         tag.method_10566("Morph", this.morph.toNBT());
      }

      tag.method_10582("Sound", this.sound);
      tag.method_10556("Read", this.read);
      tag.method_10582("Marker", this.marker);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Morph")) {
         this.morph = MorphManager.INSTANCE.morphFromNBT(tag.method_10562("Morph"));
      }

      this.sound = tag.method_10558("Sound");
      this.read = tag.method_10577("Read");
      this.marker = tag.method_10558("Marker");
   }
}
