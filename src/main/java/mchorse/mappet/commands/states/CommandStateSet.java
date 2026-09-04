package mchorse.mappet.commands.states;

import mchorse.mappet.api.states.States;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandStateSet extends CommandStateBase {
   public String getName() {
      return "set";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.state.set";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}state set{r} {7}<target> <id> <value>{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[1];

      for(States states : CommandState.getStatesList(server, sender, args[0])) {
         try {
            states.setNumber(id, Double.parseDouble(args[2]));
         } catch (NumberFormatException var8) {
            states.setString(id, String.join(" ", SubCommandBase.dropFirstArguments(args, 2)));
         }

         this.getL10n().info(sender, "states.set", new Object[]{id, states.values.get(id)});
      }
   }
}
