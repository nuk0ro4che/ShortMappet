package mchorse.mappet.network.common.dialogue;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketPickReply implements IMessage {
   public int index;

   public PacketPickReply() {
   }

   public PacketPickReply(int index) {
      this.index = index;
   }

   public void fromBytes(ByteBuf buf) {
      this.index = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.index);
   }
}
