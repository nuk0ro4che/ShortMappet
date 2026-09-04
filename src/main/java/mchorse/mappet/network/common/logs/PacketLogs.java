package mchorse.mappet.network.common.logs;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketLogs implements IMessage {
   public String text;

   public PacketLogs() {
   }

   public PacketLogs(String line) {
      this.text = line;
   }

   public void fromBytes(ByteBuf buf) {
      this.text = ForgeByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.text);
   }
}
