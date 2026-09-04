package mchorse.mappet.commands.scripts;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandScript extends MappetSubCommandBase {
   public CommandScript() {
      this.add(new CommandScriptEval());
      this.add(new CommandScriptExec());
      this.add(new CommandScriptEngines());
   }

   public String getName() {
      return "script";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.script.help";
   }
}
