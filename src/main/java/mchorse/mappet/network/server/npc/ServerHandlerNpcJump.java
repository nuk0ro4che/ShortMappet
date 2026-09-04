package mchorse.mappet.network.server.npc;

import mchorse.mappet.entities.EntityNpc;
import mchorse.mappet.network.common.npc.PacketNpcJump;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_1297;
import net.minecraft.class_3222;

public class ServerHandlerNpcJump extends ServerMessageHandler<PacketNpcJump> {
   public void run(class_3222 player, PacketNpcJump message) {
      class_1297 npc = player.method_37908().method_8469(message.entityId);
      if (npc instanceof EntityNpc entityNpc) {
         float jumpPower = message.getJumpPower();
         if (entityNpc.method_24828()) {
            entityNpc.method_18799(entityNpc.method_18798().method_1031((double)0.0F, (double)jumpPower, (double)0.0F));
         }
      }

   }
}
