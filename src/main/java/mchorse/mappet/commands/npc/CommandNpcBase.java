package mchorse.mappet.commands.npc;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.commands.MappetCommandBase;
import mchorse.mclib.commands.utils.CommandException;

public abstract class CommandNpcBase extends MappetCommandBase {
   protected Npc getNpc(String id) throws CommandException {
      Npc npc = (Npc)Mappet.npcs.load(id);
      if (npc == null) {
         throw new CommandException("npc.missing", new Object[]{id});
      } else {
         return npc;
      }
   }
}
