package mchorse.mappet.network.common.content;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.utils.IContentType;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketContentData extends PacketContentBase {
   public String name;
   public String rename;
   public class_2487 data;
   public boolean allowed;

   public PacketContentData() {
      this.name = "";
      this.allowed = true;
   }

   public PacketContentData(IContentType type, String name) {
      super(type);
      this.name = "";
      this.allowed = true;
      this.name = name;
   }

   public PacketContentData(IContentType type, String name, class_2487 data) {
      this(type, name);
      this.data = data;
   }

   public PacketContentData rename(String rename) {
      this.rename = rename;
      return this;
   }

   public PacketContentData disallow() {
      this.allowed = false;
      return this;
   }

   public void fromBytes(ByteBuf buf) {
      super.fromBytes(buf);
      this.name = ForgeByteBufUtils.readUTF8String(buf);
      if (buf.readBoolean()) {
         this.data = NBTUtils.readInfiniteTag(buf);
      }

      if (buf.readBoolean()) {
         this.rename = ForgeByteBufUtils.readUTF8String(buf);
      }

      this.allowed = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      super.toBytes(buf);
      ForgeByteBufUtils.writeUTF8String(buf, this.name);
      buf.writeBoolean(this.data != null);
      if (this.data != null) {
         ForgeByteBufUtils.writeTag(buf, this.data);
      }

      buf.writeBoolean(this.rename != null);
      if (this.rename != null) {
         ForgeByteBufUtils.writeUTF8String(buf, this.rename);
      }

      buf.writeBoolean(this.allowed);
   }
}
