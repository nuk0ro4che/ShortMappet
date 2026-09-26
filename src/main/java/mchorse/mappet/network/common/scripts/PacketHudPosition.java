package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketHudPosition implements IMessage {
   public byte element;
   public String mod;
   public int x;
   public int y;

   public PacketHudPosition() {
   }

   public PacketHudPosition(int element, int x, int y) {
      this.element = (byte)element;
      this.x = x;
      this.y = y;
   }

   public PacketHudPosition(String mod, int x, int y) {
      this.mod = mod;
      this.x = x;
      this.y = y;
   }

   public void fromBytes(ByteBuf buf) {
      if (buf.readBoolean()) {
         this.mod = ForgeByteBufUtils.readUTF8String(buf);
      } else {
         this.element = buf.readByte();
      }

      this.x = buf.readInt();
      this.y = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.mod != null);
      if (this.mod != null) {
         ForgeByteBufUtils.writeUTF8String(buf, this.mod);
      } else {
         buf.writeByte(this.element);
      }

      buf.writeInt(this.x);
      buf.writeInt(this.y);
   }
}