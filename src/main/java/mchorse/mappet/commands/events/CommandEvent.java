package mchorse.mappet.commands.events;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandEvent extends MappetSubCommandBase {
   public CommandEvent() {
      this.add(new CommandEventStop());
      this.add(new CommandEventTrigger());
   }

   public String getName() {
      return "event";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.event.help";
   }
}
