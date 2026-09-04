package mchorse.mappet.commands.dialogues;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandDialogue extends MappetSubCommandBase {
   public CommandDialogue() {
      this.add(new CommandDialogueOpen());
   }

   public String getName() {
      return "dialogue";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.dialogue.help";
   }
}
