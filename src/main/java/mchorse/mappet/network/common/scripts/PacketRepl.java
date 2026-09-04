package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketRepl implements IMessage {
   public String code;

   public PacketRepl() {
   }

   public PacketRepl(String code) {
      this.code = code;
   }

   public void fromBytes(ByteBuf buf) {
      this.code = ForgeByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.code);
   }
}
