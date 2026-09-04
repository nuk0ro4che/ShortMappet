package mchorse.mappet.commands.crafting;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandCrafting extends MappetSubCommandBase {
   public CommandCrafting() {
      this.add(new CommandCraftingDrop());
      this.add(new CommandCraftingOpen());
   }

   public String getName() {
      return "crafting";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.crafting.help";
   }
}
