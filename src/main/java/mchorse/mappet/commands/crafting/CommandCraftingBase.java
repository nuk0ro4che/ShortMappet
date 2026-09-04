package mchorse.mappet.commands.crafting;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public abstract class CommandCraftingBase extends MappetCommandBase {
   protected CraftingTable getCraftingTable(String id) throws CommandException {
      CraftingTable craftingTable = (CraftingTable)Mappet.crafting.load(id);
      if (craftingTable == null) {
         throw new CommandException("crafting.missing", new Object[]{id});
      } else {
         return craftingTable;
      }
   }

   public int getRequiredArgs() {
      return 2;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 2 ? getListOfStringsMatchingLastWord(args, Mappet.crafting.getKeys()) : super.getTabCompletions(server, sender, args);
   }
}
