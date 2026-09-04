package mchorse.mappet.commands.scripts;

import javax.script.ScriptException;
import mchorse.mappet.Mappet;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandScriptEval extends CommandScriptBase {
   public String getName() {
      return "eval";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.script.eval";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}script eval{r} {7}<code...>{r}";
   }

   public int getRequiredArgs() {
      return 1;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      class_1297 var6 = sender.method_9228();
      Object var10000;
      if (var6 instanceof class_3222 player) {
         var10000 = player;
      } else {
         var10000 = server;
      }

      Object key = var10000;
      String code = String.join(" ", args);

      try {
         Mappet.scripts.executeRepl(key, code);
      } catch (ScriptException e) {
         throw new CommandException("script.error", new Object[]{code, e.getLineNumber(), e.getColumnNumber(), e.getMessage()});
      } catch (Exception e) {
         throw new CommandException("script.empty", new Object[]{code, e.getClass().getSimpleName() + ": " + e.getMessage()});
      }
   }
}
