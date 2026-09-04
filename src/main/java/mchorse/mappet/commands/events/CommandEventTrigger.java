package mchorse.mappet.commands.events;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.events.EventContext;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.nodes.NodeSystem;
import mchorse.mappet.commands.CommandMappet;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandEventTrigger extends CommandEventBase {
   public String getName() {
      return "trigger";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.event.trigger";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}event trigger{r} {7}<target> <id> [data]{r}";
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      NodeSystem<EventBaseNode> event = this.getEvent(args[1]);
      if (event.main == null) {
         throw new CommandException("event.empty", new Object[]{args[1]});
      }

      if (args[0].startsWith("@")) {
         for(class_1297 entity : getEntities(server, sender, args[0])) {
            DataContext context = new DataContext(entity);
            if (args.length > 2) {
               context.parse(String.join(" ", SubCommandBase.dropFirstArguments(args, 2)));
            }

            Mappet.events.execute(event, new EventContext(context));
         }
      } else {
         DataContext context = CommandMappet.createContext(server, sender, args[0]);
         if (args.length > 2) {
            context.parse(String.join(" ", SubCommandBase.dropFirstArguments(args, 2)));
         }

         Mappet.events.execute(event, new EventContext(context));
      }
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, CommandMappet.listOfPlayersAndServer(server)) : super.getTabCompletions(server, sender, args);
   }
}
