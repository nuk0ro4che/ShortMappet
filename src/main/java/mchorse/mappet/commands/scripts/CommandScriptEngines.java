package mchorse.mappet.commands.scripts;

import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import mchorse.mappet.utils.ScriptUtils;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandScriptEngines extends CommandScriptBase {
   public String getName() {
      return "engines";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.script.engines";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}script engines";
   }

   public int getRequiredArgs() {
      return 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      List<ScriptEngine> engines = ScriptUtils.getAllEngines();
      List<String> strings = new ArrayList();

      for(ScriptEngine engine : engines) {
         ScriptEngineFactory factory = engine.getFactory();
         String var10001 = factory.getEngineName();
         strings.add(var10001 + " (" + factory.getLanguageName() + ")");
      }

      this.getL10n().info(sender, "scripts.engines", new Object[]{String.join(", ", strings)});
   }
}
