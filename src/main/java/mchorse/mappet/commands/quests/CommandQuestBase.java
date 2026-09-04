package mchorse.mappet.commands.quests;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public abstract class CommandQuestBase extends MappetCommandBase {
   protected Quest getQuest(String id) throws CommandException {
      Quest quest = (Quest)Mappet.quests.load(id);
      if (quest == null) {
         throw new CommandException("quest.missing", new Object[]{id});
      } else {
         return quest;
      }
   }

   public int getRequiredArgs() {
      return 2;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 2 ? getListOfStringsMatchingLastWord(args, Mappet.quests.getKeys()) : super.getTabCompletions(server, sender, args);
   }
}
