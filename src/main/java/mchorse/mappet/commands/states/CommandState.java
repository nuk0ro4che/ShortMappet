package mchorse.mappet.commands.states;

import java.util.Collections;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.states.States;
import mchorse.mappet.commands.MappetSubCommandBase;
import mchorse.mappet.commands.factions.CommandFaction;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandState extends MappetSubCommandBase {
   public static States getStates(MinecraftServer server, class_2168 sender, String target) throws CommandException {
      return (States)getStatesList(server, sender, target).get(0);
   }

   public static List<States> getStatesList(MinecraftServer server, class_2168 sender, String target) throws CommandException {
      return target.equals("~") ? Collections.singletonList(Mappet.states) : CommandFaction.getStatesList(server, sender, target);
   }

   public CommandState() {
      this.add(new CommandStateAdd());
      this.add(new CommandStateClear());
      this.add(new CommandStateIf());
      this.add(new CommandStatePrint());
      this.add(new CommandStateSet());
      this.add(new CommandStateSub());
   }

   public String getName() {
      return "state";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.state.help";
   }
}
