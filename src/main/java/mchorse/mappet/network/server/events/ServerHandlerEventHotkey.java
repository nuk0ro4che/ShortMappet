package mchorse.mappet.network.server.events;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.misc.hotkeys.TriggerHotkey;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.network.common.events.PacketEventHotkey;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerEventHotkey extends ServerMessageHandler<PacketEventHotkey> {
   public void run(class_3222 player, PacketEventHotkey message) {
      if (message.input == TriggerHotkey.INPUT_MOUSE) {
         DataContext context = (new DataContext(player)).set("button", (double)message.keycode).set("buttonState", message.down).set("DWhell", (double)message.wheel);
         Mappet.settings.mouseInput.trigger(context);
      } else {
         DataContext context = (new DataContext(player)).set("keyCode", String.valueOf(message.keycode)).set("keyState", message.down);
         Mappet.settings.playerKeyboard.trigger(context);
      }

      Mappet.settings.hotkeys.execute(player, message.input, message.keycode, message.down);
   }
}
