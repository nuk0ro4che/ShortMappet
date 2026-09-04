package mchorse.mappet.commands.quests;

import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1657;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandQuestComplete extends CommandQuestBase {
   public String getName() {
      return "complete";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.quest.complete";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}quest complete{r} {7}<player> <id>{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      for(class_1657 player : getPlayers(server, sender, args[0])) {
         ICharacter character = Character.get(player);
         if (character != null && character.getQuests().complete(args[1], player)) {
            this.getL10n().success(sender, "quest.completed", new Object[]{args[1], player.method_5477()});
         }
      }
   }
}
