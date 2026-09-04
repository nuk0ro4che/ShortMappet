package mchorse.mappet.entities;

import net.minecraft.class_2168;
import net.minecraft.server.MinecraftServer;

public class CommandNpc {
   public String command = "";

   public CommandNpc() {
   }

   public CommandNpc(String command) {
      this.command = command;
   }

   public void apply(EntityNpc npc) {
      MinecraftServer server = npc.method_5682();
      if (server != null && !this.command.isEmpty()) {
         server.method_3734().method_44252(getCommandSender(npc), this.command);
      }

   }

   public static class_2168 getCommandSender(EntityNpc npc) {
      return npc.method_5671().method_9206(4);
   }
}
