package mchorse.mappet.commands.npc;

import mchorse.mappet.entities.EntityNpc;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandNpcDespawn extends CommandNpcBase {
   public String getName() {
      return "despawn";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.npc.despawn";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}npc despawn{r} {7}<target>{r}";
   }

   public int getRequiredArgs() {
      return 1;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      for(class_1297 entity : getEntities(server, sender, args[0])) {
         if (entity instanceof EntityNpc) {
            ((EntityNpc)entity).method_31472();
         }
      }
   }
}
