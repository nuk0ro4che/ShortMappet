package mchorse.mappet.commands.data;

import java.time.Instant;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mappet.compat.CommandBase;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.quests.PacketQuests;
import mchorse.mclib.commands.McCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class CommandDataClear extends MappetCommandBase {
   public static void clear(class_3222 player, boolean inventory) {
      ICharacter character = Character.get(player);
      if (character != null) {
         character.getStates().clear();
         character.getQuests().quests.clear();
         Dispatcher.sendTo(new PacketQuests(character.getQuests()), player);
      }

      if (inventory) {
         player.method_31548().method_5448();
      }

   }

   public String getName() {
      return "clear";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.data.clear";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}data clear{r} {7}[inventory]{r}";
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      boolean inventory = args.length != 0 && CommandBase.parseBoolean(args[0]);
      Mappet.states.clear();
      Mappet.data.updateLastClear(inventory);

      for(class_3222 player : server.method_3760().method_14571()) {
         clear(player, inventory);
         ICharacter character = Character.get(player);
         if (character != null) {
            character.updateLastClear(Instant.now());
         }
      }

      this.getL10n().info(sender, inventory ? "data.cleared_inventory" : "data.cleared", new Object[0]);
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, McCommandBase.BOOLEANS) : super.getTabCompletions(server, sender, args);
   }
}
