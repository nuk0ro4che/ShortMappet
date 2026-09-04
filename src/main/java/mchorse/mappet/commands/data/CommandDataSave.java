package mchorse.mappet.commands.data;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.data.Data;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandDataSave extends CommandDataBase {
   public String getName() {
      return "save";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.data.save";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}data save{r} {7}<id>{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[0];
      Data data = new Data();
      class_3222 player = getCommandSenderAsPlayer(sender);
      data.save(player);
      if (Mappet.data.save(id, data)) {
         this.getL10n().success(sender, "data.saved", new Object[]{id});
      }

   }
}
