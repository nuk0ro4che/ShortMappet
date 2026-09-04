package mchorse.mappet.network.common.events;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketEventHotkey implements IMessage {
   public int input;
   public int keycode;
   public boolean down;
   public int wheel;

   public PacketEventHotkey() {
   }

   public PacketEventHotkey(int keycode, boolean down) {
      this(0, keycode, down);
   }

   public PacketEventHotkey(int input, int keycode, boolean down) {
      this(input, keycode, down, 0);
   }

   public PacketEventHotkey(int input, int keycode, boolean down, int wheel) {
      this.input = input;
      this.keycode = keycode;
      this.down = down;
      this.wheel = wheel;
   }

   public void fromBytes(ByteBuf buf) {
      this.input = buf.readInt();
      this.keycode = buf.readInt();
      this.down = buf.readBoolean();
      this.wheel = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.input);
      buf.writeInt(this.keycode);
      buf.writeBoolean(this.down);
      buf.writeInt(this.wheel);
   }
}
