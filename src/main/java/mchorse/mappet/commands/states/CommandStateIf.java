package mchorse.mappet.commands.states;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import mchorse.mclib.math.IValue;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandStateIf extends CommandStateBase {
   public String getName() {
      return "if";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.state.if";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}state if{r} {7}<target> <id> <expression>{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[1];
      String expression = String.join(" ", SubCommandBase.dropFirstArguments(args, 2));
      class_1297 entity = sender.method_9228();
      DataContext context = entity instanceof class_1657 player ? new DataContext(player) : new DataContext(server);

      for(States states : CommandState.getStatesList(server, sender, args[0])) {
         if (!states.values.containsKey(id)) {
            throw new CommandException("states.missing", new Object[]{id});
         }

         Object previous = states.values.get(id);
         if (previous instanceof Number) {
            context.set("value", ((Number)previous).doubleValue());
         } else if (previous instanceof String) {
            context.set("value", (String)previous);
         }

         IValue result = Mappet.expressions.set(context).parse(expression);
         if (!result.booleanValue()) {
            throw new CommandException("states.false", new Object[]{id});
         }
      }

      this.getL10n().info(sender, "states.true", new Object[]{id});
   }
}
