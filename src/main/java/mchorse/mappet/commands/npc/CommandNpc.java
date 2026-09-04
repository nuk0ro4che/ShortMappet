package mchorse.mappet.commands.npc;

import mchorse.mappet.commands.MappetSubCommandBase;
import net.minecraft.class_2168;

public class CommandNpc extends MappetSubCommandBase {
   public CommandNpc() {
      this.add(new CommandNpcDespawn());
      this.add(new CommandNpcEdit());
      this.add(new CommandNpcState());
      this.add(new CommandNpcSummon());
   }

   public String getName() {
      return "npc";
   }

   public String getUsage(class_2168 sender) {
      return "mappet.commands.mp.npc.help";
   }
}
