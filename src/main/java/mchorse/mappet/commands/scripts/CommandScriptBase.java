package mchorse.mappet.commands.scripts;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.commands.MappetCommandBase;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public abstract class CommandScriptBase extends MappetCommandBase {
   public int getRequiredArgs() {
      return 2;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 2 ? getListOfStringsMatchingLastWord(args, Mappet.scripts.getKeys()) : super.getTabCompletions(server, sender, args);
   }
}
