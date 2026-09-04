package mchorse.mappet.network.client.items;

import mchorse.mappet.network.common.items.PacketScriptedItemInfo;
import mchorse.mappet.utils.NBTUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_746;

public class ClientHandlerScriptedItemInfo extends ClientMessageHandler<PacketScriptedItemInfo> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketScriptedItemInfo message) {
      class_1297 entity = player.method_37908().method_8469(message.entity);
      if (entity instanceof class_1309 base) {
         class_1799 stack = base.method_6047();
         if (stack.method_7960()) {
            return;
         }

         if (message.stackTag != null) {
            stack.method_7980(message.stackTag);
         }

         NBTUtils.saveScriptedItemProps(stack, message.tag);
      }

   }
}
