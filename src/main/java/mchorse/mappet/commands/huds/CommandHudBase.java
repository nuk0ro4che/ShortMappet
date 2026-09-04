package mchorse.mappet.commands.huds;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public abstract class CommandHudBase extends MappetCommandBase {
   protected HUDScene getHud(String id) throws CommandException {
      HUDScene scene = (HUDScene)Mappet.huds.load(id);
      if (scene == null) {
         throw new CommandException("hud.missing", new Object[]{id});
      } else {
         return scene;
      }
   }

   public int getRequiredArgs() {
      return 2;
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, server.method_3760().method_14580());
      } else {
         if (args.length == 2) {
            if (this instanceof CommandHudSetup) {
               return getListOfStringsMatchingLastWord(args, Mappet.huds.getKeys());
            }

            class_3222 player = server.method_3760().method_14566(args[0]);
            if (player != null) {
               Character character = Character.get(player);
               if (character != null) {
                  return getListOfStringsMatchingLastWord(args, character.getDisplayedHUDs().keySet());
               }
            }
         }

         return super.getTabCompletions(server, sender, args);
      }
   }
}
