package mchorse.mappet.commands.dialogues;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.dialogues.Dialogue;
import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.commands.CommandMappet;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandDialogueOpen extends CommandDialogueBase {
   public String getName() {
      return "open";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.dialogue.open";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}dialogue open{r} {7}<player> <id> [data]{r}";
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[1];
      Dialogue dialogue = this.getDialogue(id);
      if (dialogue.main == null) {
         throw new CommandException("dialogue.empty", new Object[]{id});
      }

      for(class_3222 player : getPlayers(server, sender, args[0])) {
         DataContext context = new DataContext(player);
         if (args.length > 2) {
            context.parse(String.join(" ", SubCommandBase.dropFirstArguments(args, 2)));
         }

         Mappet.dialogues.open(player, dialogue, new DialogueContext(context));
      }
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, CommandMappet.listOfPlayers(server)) : super.getTabCompletions(server, sender, args);
   }
}
