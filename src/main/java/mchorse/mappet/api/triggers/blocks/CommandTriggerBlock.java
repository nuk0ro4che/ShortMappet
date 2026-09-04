package mchorse.mappet.api.triggers.blocks;

import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class CommandTriggerBlock extends StringTriggerBlock {
   public CommandTriggerBlock() {
   }

   public CommandTriggerBlock(String string) {
      super(string);
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      return !this.string.startsWith("/") ? "/" + this.string : this.string;
   }

   public void trigger(DataContext context) {
      if (!this.string.isEmpty()) {
         context.execute(this.string);
      }

   }

   protected String getKey() {
      return "Command";
   }
}
