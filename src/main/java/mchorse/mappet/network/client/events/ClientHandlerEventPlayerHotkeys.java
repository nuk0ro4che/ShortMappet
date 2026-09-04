package mchorse.mappet.network.client.events;

import mchorse.mappet.client.InputTriggerHandler;
import mchorse.mappet.client.KeyboardHandler;
import mchorse.mappet.network.common.events.PacketEventHotkeys;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerEventPlayerHotkeys extends ClientMessageHandler<PacketEventHotkeys> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketEventHotkeys message) {
      KeyboardHandler.hotkeys.clear();
      KeyboardHandler.hotkeys.addAll(message.hotkeys);
      InputTriggerHandler.setActive(message.keyboardInput, message.mouseInput);
      KeyboardHandler.clientPlayerJournal = !message.journalTrigger;
   }
}
