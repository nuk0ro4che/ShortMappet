package mchorse.mappet.network.common.logs;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketRequestLogs implements IMessage {
   public String lastLogTime = "";

   public PacketRequestLogs setLastDate(String lastLogTime) {
      this.lastLogTime = lastLogTime;
      return this;
   }

   public void fromBytes(ByteBuf buf) {
      this.lastLogTime = ForgeByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.lastLogTime);
   }
}
