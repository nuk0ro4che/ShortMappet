package mchorse.mappet.network.common.content;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mclib.network.ForgeByteBufUtils;

public class PacketContentFolder extends PacketContentBase {
   public String name = "";
   public String path = "";
   public String rename;
   public Boolean delete = false;

   public PacketContentFolder() {
   }

   public PacketContentFolder(IContentType type, String name, String path) {
      super(type);
      this.path = path;
      this.name = name;
   }

   public PacketContentFolder rename(String rename) {
      this.rename = rename;
      return this;
   }

   public PacketContentFolder delete() {
      this.delete = true;
      return this;
   }

   public void fromBytes(ByteBuf buf) {
      super.fromBytes(buf);
      this.name = ForgeByteBufUtils.readUTF8String(buf);
      this.path = ForgeByteBufUtils.readUTF8String(buf);
      if (buf.readBoolean()) {
         this.rename = ForgeByteBufUtils.readUTF8String(buf);
      }

      this.delete = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      super.toBytes(buf);
      ForgeByteBufUtils.writeUTF8String(buf, this.name);
      ForgeByteBufUtils.writeUTF8String(buf, this.path);
      buf.writeBoolean(this.rename != null);
      if (this.rename != null) {
         ForgeByteBufUtils.writeUTF8String(buf, this.rename);
      }

      buf.writeBoolean(this.delete);
   }
}
