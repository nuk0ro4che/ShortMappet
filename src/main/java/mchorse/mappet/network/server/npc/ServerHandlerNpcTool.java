package mchorse.mappet.network.server.npc;

import mchorse.mappet.Mappet;
import mchorse.mappet.network.common.npc.PacketNpcTool;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class ServerHandlerNpcTool extends ServerMessageHandler<PacketNpcTool> {
   public void run(class_3222 player, PacketNpcTool message) {
      class_1799 stack = player.method_6047();
      if (stack.method_7909() == Mappet.npcTool) {
         class_2487 tag = stack.method_7969();
         if (tag == null) {
            tag = new class_2487();
            stack.method_7980(tag);
         }

         if (message.npc.isEmpty()) {
            tag.method_10551("Npc");
         } else {
            tag.method_10582("Npc", message.npc);
         }

         if (message.state.isEmpty()) {
            tag.method_10551("State");
         } else {
            tag.method_10582("State", message.state);
         }
      }

   }
}
