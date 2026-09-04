package mchorse.mappet.commands.states;

import mchorse.mappet.api.states.States;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import net.minecraft.server.MinecraftServer;

public class CommandStatePrint extends CommandStateBase {
   public String getName() {
      return "print";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.state.print";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}state print{r} {7}<target>{r}";
   }


   public int getRequiredArgs() {
      return 1;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      class_5250 component = this.getL10n().info("states.print", new Object[]{args[0]}).method_27661();
      int i = 0;

      for(States states : CommandState.getStatesList(server, sender, args[0])) {
         for(String key : states.values.keySet()) {
            Object value = states.values.get(key);
            component.method_10852(class_2561.method_43470(key + " " + (value instanceof String ? "(s)" : "(n)") + " §7=§r " + String.valueOf(value) + "\n"));
            ++i;
         }
      }

      if (i > 0) {
         sender.method_45068(component);
      }

   }
}
