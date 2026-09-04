package mchorse.mappet.network.server.items;

import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.items.PacketScriptedItemInfo;
import mchorse.mappet.utils.NBTUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.network.ServerMessageHandler;
import mchorse.mclib.utils.OpHelper;
import net.minecraft.class_1799;
import net.minecraft.class_3222;

public class ServerHandlerScriptedItemInfo extends ServerMessageHandler<PacketScriptedItemInfo> {
   public void run(class_3222 player, PacketScriptedItemInfo message) {
      if (OpHelper.isPlayerOp(player)) {
         class_1799 stack = player.method_6047();
         if (message.stackTag != null) {
            stack.method_7980(message.stackTag);
         }

         if (NBTUtils.saveScriptedItemProps(stack, message.tag)) {
            IMessage packet = new PacketScriptedItemInfo(message.tag, message.stackTag, player.method_5628());
            Dispatcher.sendTo(packet, player);
            Dispatcher.sendToTracked(player, packet);
         }

      }
   }
}
