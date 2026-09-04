package mchorse.mappet.commands.huds;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandHud extends MappetSubCommandBase {
   public CommandHud() {
      this.add(new CommandHudClose());
      this.add(new CommandHudMorph());
      this.add(new CommandHudSetup());
   }

   public String getName() {
      return "hud";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.hud.help";
   }
}
