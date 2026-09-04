package mchorse.mappet.commands.morphs;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandMorphAdd extends MappetSubCommandBase {
   public CommandMorphAdd() {
      this.add(new CommandMorphAddEntity());
      this.add(new CommandMorphAddWorld());
   }

   public String getName() {
      return "add";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.morph.add.help";
   }
}
