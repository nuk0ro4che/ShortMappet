package mchorse.mappet.commands;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import mchorse.mclib.commands.utils.L10n;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public abstract class MappetSubCommandBase extends SubCommandBase {
   public static class_1297 getEntity(MinecraftServer server, class_2168 source, String selector) throws CommandException {
      return MappetCommandBase.getEntity(server, source, selector);
   }

   public static <T extends class_1297> T getEntity(MinecraftServer server, class_2168 source, String selector, Class<T> type) throws CommandException {
      return (T)MappetCommandBase.getEntity(server, source, selector, type);
   }

   public static class_3222 getPlayer(MinecraftServer server, class_2168 source, String selector) throws CommandException {
      return MappetCommandBase.getPlayer(server, source, selector);
   }

   public static List<class_1297> getEntities(MinecraftServer server, class_2168 source, String selector) throws CommandException {
      return MappetCommandBase.getEntities(server, source, selector);
   }

   public static List<class_3222> getPlayers(MinecraftServer server, class_2168 source, String selector) throws CommandException {
      return MappetCommandBase.getPlayers(server, source, selector);
   }

   public L10n getL10n() {
      return Mappet.l10n;
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}" + this.getName() + "{r} {7}...{r}";
   }
}
