package mchorse.mappet.network.common.content;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mclib.network.ForgeByteBufUtils;

public class PacketContentNames extends PacketContentBase {
   public List<String> names = new ArrayList();

   public PacketContentNames() {
   }

   public PacketContentNames(IContentType type, List<String> names) {
      super(type);
      this.names.addAll(names);
   }

   public PacketContentNames(IContentType type, List<String> names, int requestId) {
      super(type, requestId);
      this.names.addAll(names);
   }

   public void fromBytes(ByteBuf buf) {
      super.fromBytes(buf);
      int i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         this.names.add(ForgeByteBufUtils.readUTF8String(buf));
      }

   }

   public void toBytes(ByteBuf buf) {
      super.toBytes(buf);
      buf.writeInt(this.names.size());

      for(String name : this.names) {
         ForgeByteBufUtils.writeUTF8String(buf, name);
      }

   }
}
