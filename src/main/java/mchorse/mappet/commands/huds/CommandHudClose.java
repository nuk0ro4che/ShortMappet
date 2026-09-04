package mchorse.mappet.commands.huds;

import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandHudClose extends CommandHudBase {
   public String getName() {
      return "close";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.hud.close";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}hud close{r} {7}<target> [id]{r}";
   }

   public int getRequiredArgs() {
      return 1;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      for(class_3222 player : getPlayers(server, sender, args[0])) {
         ICharacter character = Character.get(player);
         if (character != null) {
            if (args.length > 1) {
               character.closeHUD(args[1]);
            } else {
               character.closeAllHUD();
            }
         }
      }
   }
}
