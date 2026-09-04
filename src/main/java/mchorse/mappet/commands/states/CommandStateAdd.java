package mchorse.mappet.commands.states;

import mchorse.mappet.api.states.States;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandStateAdd extends CommandStateBase {
   public String getName() {
      return "add";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.state.add";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}state add{r} {7}<target> <id> <number>{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[1];
      double value = CommandBase.parseDouble(args[2]);

      for(States states : CommandState.getStatesList(server, sender, args[0])) {
         double previous = states.getNumber(id);
         states.add(id, this.processValue(value));
         this.getL10n().info(sender, "states.changed", new Object[]{id, previous, states.getNumber(id)});
      }
   }

   protected double processValue(double value) {
      return value;
   }
}
