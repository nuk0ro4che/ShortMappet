package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketHudPosition implements IMessage {
   public byte element;
   public int x;
   public int y;

   public PacketHudPosition() {
   }

   public PacketHudPosition(int element, int x, int y) {
      this.element = (byte)element;
      this.x = x;
      this.y = y;
   }

   public void fromBytes(ByteBuf buf) {
      this.element = buf.readByte();
      this.x = buf.readInt();
      this.y = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.element);
      buf.writeInt(this.x);
      buf.writeInt(this.y);
   }
}
