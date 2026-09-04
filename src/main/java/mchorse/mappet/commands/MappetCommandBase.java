package mchorse.mappet.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mclib.commands.McCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import mchorse.mclib.commands.utils.EntitySelectorUtils;
import mchorse.mclib.commands.utils.L10n;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public abstract class MappetCommandBase extends McCommandBase {
   public static class_3222 getPlayer(MinecraftServer server, class_2168 source, String selector) throws CommandException {
      return (class_3222)getPlayers(server, source, selector).get(0);
   }

   public static List<class_3222> getPlayers(MinecraftServer server, class_2168 source, String selector) throws CommandException {
      if (!selector.startsWith("@")) {
         class_3222 player = EntitySelectorUtils.getPlayer(source, selector);
         if (player == null) {
            throw new CommandException("commands.generic.player.notFound", new Object[]{selector});
         }

         return Collections.singletonList(player);
      }

      List<? extends class_1297> entities = EntitySelectorUtils.getEntities(source, selector);
      List<class_3222> players = new ArrayList();

      for(class_1297 entity : entities) {
         if (entity instanceof class_3222 player) {
            players.add(player);
         }
      }

      if (players.isEmpty()) {
         throw new CommandException("commands.generic.player.notFound", new Object[]{selector});
      }

      return players;
   }

   public static class_1297 getEntity(MinecraftServer server, class_2168 source, String selector) throws CommandException {
      return (class_1297)getEntities(server, source, selector).get(0);
   }

   public static List<class_1297> getEntities(MinecraftServer server, class_2168 source, String selector) throws CommandException {
      if (selector.startsWith("@")) {
         List<? extends class_1297> selected = EntitySelectorUtils.getEntities(source, selector);
         if (selected.isEmpty()) {
            throw new CommandException("commands.generic.entity.notFound", new Object[]{selector});
         }

         return new ArrayList(selected);
      }

      class_3222 player = server.method_3760().method_14566(selector);
      if (player != null) {
         return Collections.singletonList(player);
      }

      for(class_3218 world : server.method_3738()) {
         for(class_1297 entity : world.method_27909()) {
            if (selector.equals(entity.method_5845()) || selector.equals(entity.method_5477().getString())) {
               return Collections.singletonList(entity);
            }
         }
      }

      throw new CommandException("commands.generic.entity.notFound", new Object[]{selector});
   }

   public static <T extends class_1297> T getEntity(MinecraftServer server, class_2168 source, String selector, Class<T> type) throws CommandException {
      class_1297 entity = getEntity(server, source, selector);
      if (!type.isInstance(entity)) {
         throw new CommandException("commands.generic.entity.invalidType", new Object[]{selector});
      } else {
         return (T)(type.cast(entity));
      }
   }

   public L10n getL10n() {
      return Mappet.l10n;
   }
}
