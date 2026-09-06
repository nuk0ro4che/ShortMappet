package mchorse.mappet.commands.scripts;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.scripts.client.ClientScriptExecutor;
import mchorse.mappet.commands.MappetSubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;


public class CommandClientScript extends MappetSubCommandBase {
   public CommandClientScript() {
      this.add(new Exec());
   }

   public String getName() {
      return "clientscript";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.clientscript.help";
   }

   private static class Exec extends CommandScriptBase {
      public String getName() {
         return "exec";
      }

      public String getUsage(class_2168 sender) {
         return "mappet.commands.mp.clientscript.exec";
      }

      
      public int getRequiredArgs() {
         return 1;
      }

      public String getSyntax() {
         return "{l}{6}/{r}mp {8}clientscript exec{r} {7}<id> [function]{r}";
      }

      public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
         if (args.length < 1 || args[0].isEmpty()) {
            throw new CommandException("script.empty", new Object[]{"", "Укажи название клиентского скрипта."});
         }
         class_1297 entity = sender.method_9228();
         if (!(entity instanceof class_3222)) {
            throw new CommandException("script.empty", new Object[]{args[0], "Команду должен выполнить игрок, чей клиент запустит скрипт."});
         }
         if (Mappet.clientScripts == null) {
            throw new CommandException("script.empty", new Object[]{args[0], "Хранилище клиентских скриптов ещё не загружено."});
         }

         String id = args[0];
         String function = args.length > 1 && !args[1].isEmpty() ? args[1] : "main";
         Script script = Mappet.clientScripts.load(id);
         if (script == null) {
            throw new CommandException("script.empty", new Object[]{id, "Клиентский скрипт с таким ID не найден."});
         }

         if (!ClientScriptExecutor.execute((class_3222)entity, id, function)) {
            throw new CommandException("script.empty", new Object[]{id, "Не удалось отправить клиентский скрипт игроку."});
         }
      }

      public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
         if (args.length == 1 && Mappet.clientScripts != null) {
            List<String> clientScripts = new java.util.ArrayList<String>();
            for (String id : Mappet.clientScripts.getKeys()) {
               if (id.endsWith("/")) {
                  continue;
               }
               Script script = Mappet.clientScripts.load(id);
               if (script != null && script.client) {
                  clientScripts.add(id);
               }
            }
            return getListOfStringsMatchingLastWord(args, clientScripts);
         }
         return java.util.Collections.emptyList();
      }
   }
}
