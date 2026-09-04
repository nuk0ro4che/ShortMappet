package mchorse.mappet.commands.npc;

import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.api.npcs.NpcLexer;
import mchorse.mappet.api.npcs.NpcState;
import mchorse.mappet.entities.EntityNpc;
import mchorse.mclib.commands.utils.CommandException;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_2487;
import net.minecraft.server.MinecraftServer;

public class CommandNpcState extends CommandNpcBase {
   public String getName() {
      return "state";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.npc.state";
   }

   public String getSyntax() {
      return "{l}{6}/{r}mp {8}npc state{r} {7}<target> <state>{r}";
   }

   public int getRequiredArgs() {
      return 2;
   }

   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }

   public void executeCommand(MinecraftServer server, class_2168 sender, String[] args) throws CommandException {
      for(class_1297 target : getEntities(server, sender, args[0])) {
         if (target instanceof EntityNpc entity) {
            NpcLexer lexer = NpcLexer.parse(args[1], entity.getNpcId());
            Npc npc = this.getNpc(lexer.id);
            NpcState state = (NpcState)npc.states.get(lexer.state);
            if (state == null) {
               throw new CommandException("npc.missing_state", new Object[]{args[1]});
            }

            if (!lexer.properties.isEmpty()) {
               class_2487 tag = state.partialSerializeNBT(lexer.properties);
               class_2487 original = entity.getState().serializeNBT();

               for(String key : tag.method_10541()) {
                  original.method_10566(key, tag.method_10580(key));
               }

               state = new NpcState();
               state.deserializeNBT(original);
            }

            state.id.set(entity.getNpcId());
            entity.setState(state, true);
         }
      }
   }
}
