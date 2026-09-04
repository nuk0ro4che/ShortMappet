package mchorse.mappet.compat;

import java.util.ArrayList;
import java.util.List;
import mchorse.mclib.commands.McCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import mchorse.mclib.commands.utils.EntitySelectorUtils;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public final class CommandBase {
   private CommandBase() {
   }

   public static boolean parseBoolean(String value) throws CommandException {
      return McCommandBase.parseBoolean(value);
   }

   public static int parseInt(String value) throws CommandException {
      return McCommandBase.parseInt(value);
   }

   public static int parseInt(String value, int min, int max) throws CommandException {
      return McCommandBase.parseInt(value, min, max);
   }

   public static double parseDouble(String value) throws CommandException {
      return McCommandBase.parseDouble(value);
   }

   public static double parseDouble(double base, String value, boolean center) throws CommandException {
      return McCommandBase.parseDouble(base, value, center);
   }

   public static class_1297 getEntity(MinecraftServer server, Object sender, String target) {
      return EntitySelectorUtils.getEntity(source(server, sender), target);
   }

   public static class_3222 getPlayer(MinecraftServer server, Object sender, String target) {
      return EntitySelectorUtils.getPlayer(source(server, sender), target);
   }

   public static List<class_3222> getPlayers(MinecraftServer server, Object sender, String target) {
      List<class_3222> players = new ArrayList();

      for(class_1297 entity : EntitySelectorUtils.getEntities(source(server, sender), target)) {
         if (entity instanceof class_3222 player) {
            players.add(player);
         }
      }

      return players;
   }

   private static class_2168 source(MinecraftServer server, Object sender) {
      class_2168 var10000;
      if (sender instanceof class_2168 source) {
         var10000 = source;
      } else {
         var10000 = server.method_3739();
      }

      return var10000;
   }
}
