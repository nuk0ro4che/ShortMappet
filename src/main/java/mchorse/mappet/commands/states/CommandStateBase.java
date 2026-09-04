package mchorse.mappet.commands.states;

import java.util.List;
import mchorse.mappet.api.states.States;
import mchorse.mappet.commands.CommandMappet;
import mchorse.mappet.commands.MappetCommandBase;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public abstract class CommandStateBase extends MappetCommandBase {
   public boolean isUsernameIndex(String[] args, int index) {
      return args.length > 0 && !args[0].equals("~") && index == 0;
   }

   public int getRequiredArgs() {
      return 3;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, CommandMappet.listOfPlayersAndServer(server));
      } else {
         if (args.length == 2) {
            try {
               States states = CommandState.getStates(server, sender, args[0]);
               return getListOfStringsMatchingLastWord(args, states.values.keySet());
            } catch (Exception var5) {
            }
         }

         return super.getTabCompletions(server, sender, args);
      }
   }
}
