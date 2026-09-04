package mchorse.mappet.commands.factions;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.states.States;
import mchorse.mappet.commands.MappetSubCommandBase;
import mchorse.mappet.utils.EntityUtils;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandFaction extends MappetSubCommandBase {
   public static States getStates(MinecraftServer server, class_2168 sender, String target) throws CommandException {
      return (States)getStatesList(server, sender, target).get(0);
   }

   public static List<States> getStatesList(MinecraftServer server, class_2168 sender, String target) throws CommandException {
      List<States> states = new ArrayList();

      for(class_1297 entity : getEntities(server, sender, target)) {
         States entityStates = EntityUtils.getStates(entity);
         if (entityStates != null) {
            states.add(entityStates);
         }
      }

      if (states.isEmpty()) {
         throw new CommandException("states.invalid_target", new Object[]{target});
      }

      return states;
   }

   public CommandFaction() {
      this.add(new CommandFactionAdd());
      this.add(new CommandFactionClear());
      this.add(new CommandFactionSet());
   }

   public String getName() {
      return "faction";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.faction.help";
   }
}
