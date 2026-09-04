package mchorse.mappet.commands.quests;

import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1657;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandQuestAccept extends CommandQuestBase {
   public String getName() {
      return "accept";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.quest.accept";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}quest accept{r} {7}<player> <id>{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[1];
      Quest quest = this.getQuest(id);

      for(class_1657 player : getPlayers(server, sender, args[0])) {
         ICharacter character = Character.get(player);
         if (character != null && character.getQuests().add(quest, player)) {
            this.getL10n().success(sender, "quest.accepted", new Object[]{id, player.method_5477()});
         }
      }
   }
}
