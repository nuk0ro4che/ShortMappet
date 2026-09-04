package mchorse.mappet.network.common.content;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mclib.network.ForgeByteBufUtils;

public class PacketContentRequestData extends PacketContentBase {
   public String name = "";

   public PacketContentRequestData() {
   }

   public PacketContentRequestData(IContentType type, String name) {
      super(type);
      this.name = name;
   }

   public void fromBytes(ByteBuf buf) {
      super.fromBytes(buf);
      this.name = ForgeByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      super.toBytes(buf);
      ForgeByteBufUtils.writeUTF8String(buf, this.name);
   }
}
