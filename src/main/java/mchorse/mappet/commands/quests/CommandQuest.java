package mchorse.mappet.commands.quests;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandQuest extends MappetSubCommandBase {
   public CommandQuest() {
      this.add(new CommandQuestAccept());
      this.add(new CommandQuestComplete());
      this.add(new CommandQuestDecline());
   }

   public String getName() {
      return "quest";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.quest.help";
   }
}
