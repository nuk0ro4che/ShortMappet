package mchorse.mappet.commands.data;

import java.util.List;
import java.util.stream.Collectors;
import mchorse.mappet.api.data.Data;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.commands.McCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1657;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandDataLoad extends CommandDataBase {
   public String getName() {
      return "load";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.data.load";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}data load{r} {7}<id> [global] [player]{r}";
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 2;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      boolean global = args.length <= 1 || CommandBase.parseBoolean(args[1]);
      String id = args[0];
      Data data = this.getData(id);
      if (args.length > 2) {
         for(class_1657 player : getPlayers(server, sender, args[2])) {
            data.apply(player, global);
         }
      } else {
         data.apply(getCommandSenderAsPlayer(sender), global);
      }
      this.getL10n().success(sender, "data.loaded", new Object[]{id});
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      if (args.length == 2) {
         return getListOfStringsMatchingLastWord(args, McCommandBase.BOOLEANS);
      } else if (args.length == 3) {
         List<String> list = (List)server.method_3760().method_14571().stream().map((player) -> player.method_5477().getString()).collect(Collectors.toList());
         return getListOfStringsMatchingLastWord(args, list);
      } else {
         return super.getTabCompletions(server, sender, args);
      }
   }
}
