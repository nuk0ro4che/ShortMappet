package mchorse.mappet.commands;

import java.util.List;
import java.util.stream.Collectors;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.commands.crafting.CommandCrafting;
import mchorse.mappet.commands.data.CommandData;
import mchorse.mappet.commands.dialogues.CommandDialogue;
import mchorse.mappet.commands.events.CommandEvent;
import mchorse.mappet.commands.factions.CommandFaction;
import mchorse.mappet.commands.huds.CommandHud;
import mchorse.mappet.commands.morphs.CommandMorph;
import mchorse.mappet.commands.npc.CommandNpc;
import mchorse.mappet.commands.quests.CommandQuest;
import mchorse.mappet.commands.scripts.CommandScript;
import mchorse.mappet.commands.scripts.CommandClientScript;
import mchorse.mappet.commands.sounds.CommandCustomPlaySound;
import mchorse.mappet.commands.states.CommandState;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandMappet extends MappetSubCommandBase {
   public static DataContext createContext(MinecraftServer server, class_2168 sender, String argument) throws CommandException {
      if (argument.equals("~")) {
         return new DataContext(server);
      } else if (argument.equals("@s")) {
         class_1297 entity = sender.method_9228();
         if (entity == null) {
            throw new CommandException("commands.generic.entity.notFound", new Object[0]);
         } else {
            return new DataContext(entity);
         }
      } else {
         return new DataContext(getEntity(server, sender, argument));
      }
   }

   public static List<String> listOfPlayersAndServer(MinecraftServer server) {
      List<String> list = listOfPlayers(server);
      list.add("~");
      return list;
   }

   public static List<String> listOfPlayers(MinecraftServer server) {
      return (List)server.method_3760().method_14571().stream().map((player) -> player.method_5477().getString()).collect(Collectors.toList());
   }

   public CommandMappet() {
      this.add(new CommandCrafting());
      this.add(new CommandData());
      this.add(new CommandDialogue());
      this.add(new CommandEvent());
      this.add(new CommandFaction());
      this.add(new CommandHud());
      this.add(new CommandMorph());
      this.add(new CommandNpc());
      this.add(new CommandQuest());
      this.add(new CommandScript());
      this.add(new CommandClientScript());
      this.add(new CommandState());
      this.add(new CommandCustomPlaySound());
   }

   public String getName() {
      return "mp";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.help";
   }

   public int getRequiredPermissionLevel() {
      return 2;
   }
}
