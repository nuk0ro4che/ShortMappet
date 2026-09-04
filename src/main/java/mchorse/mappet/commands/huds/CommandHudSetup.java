package mchorse.mappet.commands.huds;

import mchorse.mappet.capabilities.character.Character;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandHudSetup extends CommandHudBase {
   public String getName() {
      return "setup";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.hud.setup";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}hud setup{r} {7}<target> <id>{r}";
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      for(class_3222 player : getPlayers(server, sender, args[0])) {
         Character.get(player).setupHUD(args[1], true);
      }
   }
}
