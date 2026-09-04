package mchorse.mappet.network.client.scripts;

import mchorse.mappet.network.common.scripts.PacketEntityRotations;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_746;

public class ClientHandlerEntityRotations extends ClientMessageHandler<PacketEntityRotations> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketEntityRotations message) {
      class_1297 entity = player.method_37908().method_8469(message.entityId);
      if (entity != null) {
         


         float previousYaw = entity.method_36454();
         float previousPitch = entity.method_36455();
         float previousYawHead = entity.method_5791();
         entity.field_5982 = previousYaw;
         entity.field_6004 = previousPitch;
         entity.method_5808(entity.method_23317(), entity.method_23318(), entity.method_23321(), message.yaw, message.pitch);
         entity.method_5847(message.yawHead);
         if (entity instanceof class_1309) {
            class_1309 living = (class_1309)entity;
            living.field_6259 = previousYawHead;
            living.field_6283 = message.yawHead;
         }
      }

   }
}
