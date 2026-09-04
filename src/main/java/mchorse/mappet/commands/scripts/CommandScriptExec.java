package mchorse.mappet.commands.scripts;

import java.util.List;
import javax.script.ScriptException;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.commands.CommandMappet;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandScriptExec extends CommandScriptBase {
   public String getName() {
      return "exec";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.script.exec";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}script exec{r} {7}<target> <id> [function] [data]{r}";
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String function = args.length > 2 ? args[2] : "main";
      String data = args.length > 3 ? String.join(" ", SubCommandBase.dropFirstArguments(args, 3)) : null;

      if (args[0].startsWith("@")) {
         for (class_1297 entity : getEntities(server, sender, args[0])) {
            DataContext context = new DataContext(entity);
            if (data != null) {
               context.parse(data);
            }
            this.executeScript(args[1], function, context);
         }
      } else {
         DataContext context = CommandMappet.createContext(server, sender, args[0]);
         if (data != null) {
            context.parse(data);
         }
         this.executeScript(args[1], function, context);
      }
   }

   private void executeScript(String script, String function, DataContext context) throws CommandException {
      try {
         Mappet.scripts.execute(script, function, context);
      } catch (ScriptException e) {
         String fileName = e.getFileName() == null ? script : e.getFileName();
         e.printStackTrace();
         Mappet.logger.error(e.getMessage());
         throw new CommandException("script.error", new Object[]{fileName, e.getLineNumber(), e.getColumnNumber(), e.getMessage()});
      } catch (Exception e) {
         e.printStackTrace();
         Mappet.logger.error(e.getMessage());
         String message = e.getClass().getSimpleName() + ": " + e.getMessage();
         throw new CommandException("script.empty", new Object[]{script, message});
      }
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, CommandMappet.listOfPlayersAndServer(server)) : super.getTabCompletions(server, sender, args);
   }
}
