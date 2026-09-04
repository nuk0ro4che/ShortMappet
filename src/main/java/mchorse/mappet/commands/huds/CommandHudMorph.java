package mchorse.mappet.commands.huds;

import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_2487;
import net.minecraft.class_2522;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandHudMorph extends CommandHudBase {
   public String getName() {
      return "morph";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.hud.morph";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}hud morph{r} {7}<target> <id> <index> <nbt>{r}";
   }

   public int getRequiredArgs() {
      return 4;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String scene = args[1];
      int index = CommandBase.parseInt(args[2]);
      class_2487 tag = null;

      try {
         tag = class_2522.method_10718(String.join(" ", SubCommandBase.dropFirstArguments(args, 3)));
      } catch (Exception var9) {
      }

      for(class_3222 player : getPlayers(server, sender, args[0])) {
         Character.get(player).changeHUDMorph(scene, index, tag);
      }
   }
}
