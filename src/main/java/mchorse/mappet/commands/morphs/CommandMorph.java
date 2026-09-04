package mchorse.mappet.commands.morphs;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandMorph extends MappetSubCommandBase {
   public CommandMorph() {
      this.add(new CommandMorphAdd());
   }

   public String getName() {
      return "morph";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.morph.help";
   }
}
