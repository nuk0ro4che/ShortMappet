package mchorse.mappet.commands.states;

import mchorse.mappet.api.states.States;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandStateClear extends CommandStateBase {
   public String getName() {
      return "clear";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.state.clear";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}state clear{r} {7}<target> [id]{r}";
   }

   public int getRequiredArgs() {
      return 1;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      if (args.length > 1) {
         String id = args[1];

         for(States states : CommandState.getStatesList(server, sender, args[0])) {
            states.resetMasked(id);
         }

         this.getL10n().info(sender, "states.clear", new Object[]{id});
      } else {
         for(States states : CommandState.getStatesList(server, sender, args[0])) {
            states.clear();
         }

         this.getL10n().info(sender, "states.clear_all", new Object[]{args[0]});
      }

   }
}
