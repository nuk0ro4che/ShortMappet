package mchorse.mappet.commands.dialogues;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.dialogues.Dialogue;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public abstract class CommandDialogueBase extends MappetCommandBase {
   protected Dialogue getDialogue(String id) throws CommandException {
      Dialogue dialogue = (Dialogue)Mappet.dialogues.load(id);
      if (dialogue == null) {
         throw new CommandException("dialogue.missing", new Object[]{id});
      } else {
         return dialogue;
      }
   }

   public int getRequiredArgs() {
      return 2;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 2 ? getListOfStringsMatchingLastWord(args, Mappet.dialogues.getKeys()) : super.getTabCompletions(server, sender, args);
   }
}
