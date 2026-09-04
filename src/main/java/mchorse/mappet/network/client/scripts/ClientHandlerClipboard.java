package mchorse.mappet.network.client.scripts;

import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketClipboard;
import mchorse.mclib.client.gui.utils.GuiUtils;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerClipboard extends ClientMessageHandler<PacketClipboard> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketClipboard message) {
      if (message.action == PacketClipboard.SET) {
         set(message.text);
      } else if (message.action == PacketClipboard.REQUEST) {
         Dispatcher.sendToServer(new PacketClipboard(PacketClipboard.RESPONSE, get()));
      }
   }

   @Environment(EnvType.CLIENT)
   public static void set(String text) {
      GuiUtils.setClipboardString(text == null ? "" : text);
   }

   @Environment(EnvType.CLIENT)
   public static String get() {
      String text = GuiUtils.getClipboardString();
      return text == null ? "" : text;
   }
}
