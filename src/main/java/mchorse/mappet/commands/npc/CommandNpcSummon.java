package mchorse.mappet.commands.npc;

import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.compat.CommandBase;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_2168;
import net.minecraft.class_243;
import net.minecraft.server.MinecraftServer;

public class CommandNpcSummon extends CommandNpcBase {
   public String getName() {
      return "summon";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.npc.summon";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}npc summon{r} {7}<id> [state] [x] [y] [z]{r}";
   }

   public int getRequiredArgs() {
      return 1;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      String id = args[0];
      Npc npc = this.getNpc(id);
      if (npc.states.isEmpty()) {
         throw new CommandException("npc.empty", new Object[]{id});
      } else {
         NpcState state = (NpcState)npc.states.get(args.length >= 2 ? args[1] : "default");
         if (state == null) {
            throw new CommandException("npc.missing_state", new Object[]{id, args[1]});
         } else {
            class_243 position = sender.method_9222();
            double x = position.field_1352;
            double y = position.field_1351;
            double z = position.field_1350;
            if (args.length >= 5) {
               x = CommandBase.parseDouble(x, args[2], true);
               y = CommandBase.parseDouble(y, args[3], false);
               z = CommandBase.parseDouble(z, args[4], true);
            }

            EntityNpc entity = new EntityNpc(Mappet.npcEntity, sender.method_9225());
            entity.method_5814(x, y, z);
            entity.setNpc(npc, state);
            entity.method_37908().method_8649(entity);
            entity.initialize();
         }
      }
   }

   public List<String> getTabCompletions(MinecraftServer server, class_2168 sender, String[] args) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, Mappet.npcs.getKeys()) : super.getTabCompletions(server, sender, args);
   }
}
