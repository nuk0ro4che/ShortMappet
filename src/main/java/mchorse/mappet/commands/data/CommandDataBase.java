package mchorse.mappet.commands.data;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.data.Data;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public abstract class CommandDataBase extends MappetCommandBase {
   protected Data getData(String id) throws CommandException {
      Data data = (Data)Mappet.data.load(id);
      if (data == null) {
         throw new CommandException("data.missing", new Object[]{id});
      } else {
         return data;
      }
   }

   public int getRequiredArgs() {
      return 1;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, Mappet.data.getKeys()) : super.getTabCompletions(server, sender, args);
   }
}
