package mchorse.mappet.commands.factions;

import mchorse.mappet.api.states.States;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandFactionClear extends CommandFactionBase {
   public String getName() {
      return "clear";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.faction.clear";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}faction clear{r} {7}<target> [id]{r}";
   }

   public int getRequiredArgs() {
      return 1;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      if (args.length > 1) {
         String id = args[1];
         this.getFaction(id);

         for(States states : CommandFaction.getStatesList(server, sender, args[0])) {
            states.clearFactionScore(id);
         }

         this.getL10n().info(sender, "factions.clear", new Object[]{id});
      } else {
         for(States states : CommandFaction.getStatesList(server, sender, args[0])) {
            states.clearAllFactionScores();
         }

         this.getL10n().info(sender, "factions.clear_all", new Object[]{args[0]});
      }

   }
}
