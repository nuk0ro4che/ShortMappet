package mchorse.mappet.api.triggers.blocks;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.dialogues.Dialogue;
import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_1657;
import net.minecraft.class_3222;

public class DialogueTriggerBlock extends DataTriggerBlock {
   public DialogueTriggerBlock() {
   }

   public DialogueTriggerBlock(String string) {
      super(string);
   }

   public void trigger(DataContext context) {
      if (!this.string.isEmpty()) {
         class_1657 player = context.getPlayer();
         if (player instanceof class_3222) {
            Dialogue dialogue = (Dialogue)Mappet.dialogues.load(this.string);
            if (dialogue != null) {
               Mappet.dialogues.open((class_3222)player, dialogue, new DialogueContext(this.apply(context)));
            }
         }
      }

   }

   protected String getKey() {
      return "Dialogue";
   }
}
