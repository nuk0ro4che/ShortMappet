package mchorse.mappet.commands.events;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.utils.nodes.NodeSystem;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public abstract class CommandEventBase extends MappetCommandBase {
   protected NodeSystem<EventBaseNode> getEvent(String id) throws CommandException {
      NodeSystem<EventBaseNode> event = (NodeSystem)Mappet.events.load(id);
      if (event == null) {
         throw new CommandException("event.missing", new Object[]{id});
      } else {
         return event;
      }
   }

   public int getRequiredArgs() {
      return 2;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 2 ? getListOfStringsMatchingLastWord(args, Mappet.events.getKeys()) : super.getTabCompletions(server, sender, args);
   }
}
