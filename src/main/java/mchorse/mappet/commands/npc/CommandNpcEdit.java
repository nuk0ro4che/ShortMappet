package mchorse.mappet.commands.npc;

import mchorse.mappet.api.npcs.NpcLexer;
import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mclib.commands.SubCommandBase;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandNpcEdit extends CommandNpcBase {
   public String getName() {
      return "edit";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.npc.edit";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}npc edit{r} {7}<target> <property> <value>{r}";
   }

   public int getRequiredArgs() {
      return 3;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String property = args[1];
      if (!NpcLexer.PROPERTIES.contains(property)) {
         throw new CommandException("npc.invalid_property", new Object[]{property});
      }

      String value = String.join(" ", SubCommandBase.dropFirstArguments(args, 2));

      for(class_1297 target : getEntities(server, sender, args[0])) {
         if (target instanceof EntityNpc entity) {
            NpcState state = entity.getState();

            try {
               if (state.edit(property, value.trim())) {
                  entity.setState(state, true);
               } else {
                  throw new CommandException("npc.cant_edit", new Object[]{property, value});
               }
            } catch (Exception var11) {
               throw new CommandException("npc.cant_edit", new Object[]{property, value});
            }
         }
      }
   }
}
