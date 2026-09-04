package mchorse.mappet.network.common.events;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.misc.ServerSettings;
import mchorse.mappet.api.misc.hotkeys.TriggerHotkey;
import mchorse.mclib.network.IMessage;

public class PacketEventHotkeys implements IMessage {
   public List<TriggerHotkey> hotkeys = new ArrayList();
   public boolean keyboardInput;
   public boolean mouseInput;
   public boolean journalTrigger;

   public PacketEventHotkeys() {
   }

   public PacketEventHotkeys(ServerSettings settings) {
      this.hotkeys.addAll(settings.hotkeys.hotkeys);
      this.keyboardInput = !settings.playerKeyboard.isEmpty() || this.hasInput(TriggerHotkey.INPUT_KEYBOARD);
      this.mouseInput = !settings.mouseInput.isEmpty() || this.hasInput(TriggerHotkey.INPUT_MOUSE);
      this.journalTrigger = !settings.playerJournal.isEmpty();
   }

   public void fromBytes(ByteBuf buf) {
      int i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         this.hotkeys.add(new TriggerHotkey(buf.readInt(), buf.readBoolean(), buf.readInt()));
      }

      this.keyboardInput = buf.readBoolean();
      this.mouseInput = buf.readBoolean();
      this.journalTrigger = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.hotkeys.size());

      for(TriggerHotkey hotkey : this.hotkeys) {
         buf.writeInt(hotkey.keycode);
         buf.writeBoolean(hotkey.toggle);
         buf.writeInt(hotkey.input);
      }

      buf.writeBoolean(this.keyboardInput);
      buf.writeBoolean(this.mouseInput);
      buf.writeBoolean(this.journalTrigger);
   }

   private boolean hasInput(int input) {
      for(TriggerHotkey hotkey : this.hotkeys) {
         if (hotkey.input == input) {
            return true;
         }
      }

      return false;
   }
}
