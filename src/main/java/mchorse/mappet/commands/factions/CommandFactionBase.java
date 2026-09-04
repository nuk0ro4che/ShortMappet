package mchorse.mappet.commands.factions;

import java.util.List;
import java.util.stream.Collectors;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.factions.Faction;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public abstract class CommandFactionBase extends MappetCommandBase {
   protected Faction getFaction(String id) throws CommandException {
      Faction event = (Faction)Mappet.factions.load(id);
      if (event == null) {
         throw new CommandException("faction.missing", new Object[]{id});
      } else {
         return event;
      }
   }

   public int getRequiredArgs() {
      return 3;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      if (args.length == 1) {
         List<String> list = (List)server.method_3760().method_14571().stream().map((player) -> player.method_5477().getString()).collect(Collectors.toList());
         return getListOfStringsMatchingLastWord(args, list);
      } else {
         if (args.length == 2) {
            try {
               return getListOfStringsMatchingLastWord(args, Mappet.factions.getKeys());
            } catch (Exception var5) {
            }
         }

         return super.getTabCompletions(server, sender, args);
      }
   }
}
