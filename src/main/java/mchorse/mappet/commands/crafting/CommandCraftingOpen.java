package mchorse.mappet.commands.crafting;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.commands.CommandMappet;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandCraftingOpen extends CommandCraftingBase {
   public String getName() {
      return "open";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.crafting.open";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}crafting open{r} {7}<player> <id>{r}";
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      for(class_3222 player : getPlayers(server, sender, args[0])) {
         CraftingTable table = this.getCraftingTable(args[1]);
         table.filter(player);
         if (!table.recipes.isEmpty()) {
            Mappet.crafting.open(player, table);
         }
      }
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, CommandMappet.listOfPlayers(server)) : super.getTabCompletions(server, sender, args);
   }
}
