package mchorse.mappet.commands.factions;

import mchorse.mappet.api.states.States;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandFactionSet extends CommandFactionBase {
   public String getName() {
      return "set";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.faction.set";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}faction set{r} {7}<target> <id> <expression>{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[1];
      this.getFaction(id);
      int value = CommandBase.parseInt(args[2]);

      for(States states : CommandFaction.getStatesList(server, sender, args[0])) {
         states.setFactionScore(id, value);
         this.getL10n().info(sender, "factions.set", new Object[]{id, states.getFactionScore(id)});
      }
   }
}
