package mchorse.mappet.commands.events;

import java.util.List;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandEventStop extends CommandEventBase {
   public String getName() {
      return "stop";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.event.stop";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}event stop{r} {7}<id>{r}";
   }

   public int getRequiredArgs() {
      return 1;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      int removed = CommonProxy.eventHandler.removeExecutables(args[0]);
      Mappet.l10n.success(sender, "events.stopped", new Object[]{removed, args[0]});
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, CommonProxy.eventHandler.getIds()) : super.getTabCompletions(server, sender, args);
   }
}
