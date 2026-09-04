package mchorse.mappet.network.common.content;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketRequestStates implements IMessage {
   public String target;

   public PacketRequestStates() {
   }

   public PacketRequestStates(String target) {
      this.target = target;
   }

   public void fromBytes(ByteBuf buf) {
      this.target = ForgeByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.target);
   }
}
