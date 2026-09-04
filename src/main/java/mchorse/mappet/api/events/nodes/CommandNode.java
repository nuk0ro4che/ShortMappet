package mchorse.mappet.api.events.nodes;

import mchorse.mappet.api.events.EventContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public class CommandNode extends EventBaseNode {
   public String command = "";

   public CommandNode() {
   }

   public CommandNode(String command) {
      this.command = command;
   }

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return this.command;
   }

   public int execute(EventContext context) {
      boolean result = context.data.execute(this.command) > 0;
      context.log("Executed \"" + this.command + "\" " + (result ? "successfully" : "unsuccessfully"));
      return this.booleanToExecutionCode(result);
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      if (!this.command.isEmpty()) {
         tag.method_10582("Command", this.command);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Command")) {
         this.command = tag.method_10558("Command");
      }

   }
}
