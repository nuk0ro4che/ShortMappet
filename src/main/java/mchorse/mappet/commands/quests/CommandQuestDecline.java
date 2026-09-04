package mchorse.mappet.commands.quests;

import java.util.Collections;
import java.util.List;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1657;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandQuestDecline extends CommandQuestBase {
   public String getName() {
      return "decline";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.quest.decline";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}quest decline{r} {7}<player> <id>{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      for(class_1657 player : getPlayers(server, sender, args[0])) {
         ICharacter character = Character.get(player);
         if (character != null && character.getQuests().decline(args[1], player)) {
            this.getL10n().success(sender, "quest.declined", new Object[]{args[1], player.method_5477()});
         }
      }
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      if (args.length == 2) {
         try {
            class_1657 player = (class_1657)getPlayers(server, sender, args[0]).get(0);
            ICharacter character = Character.get(player);
            return getListOfStringsMatchingLastWord(args, character.getQuests().quests.keySet());
         } catch (Exception var6) {
            return Collections.emptyList();
         }
      } else {
         return super.getTabCompletions(server, sender, args);
      }
   }
}
