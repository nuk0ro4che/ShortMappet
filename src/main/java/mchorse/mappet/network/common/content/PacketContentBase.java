package mchorse.mappet.network.common.content;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public abstract class PacketContentBase implements IMessage {
   public IContentType type;
   public int requestId = -1;

   public PacketContentBase() {
   }

   public PacketContentBase(IContentType type) {
      this.type = type;
   }

   public PacketContentBase(IContentType type, int requestId) {
      this.type = type;
      this.requestId = requestId;
   }

   public void fromBytes(ByteBuf buf) {
      this.type = IContentType.getType(ForgeByteBufUtils.readUTF8String(buf));
      this.requestId = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.type.getName());
      buf.writeInt(this.requestId);
   }
}
