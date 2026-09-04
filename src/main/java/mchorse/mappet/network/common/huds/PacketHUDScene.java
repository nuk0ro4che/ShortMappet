package mchorse.mappet.network.common.huds;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketHUDScene implements IMessage {
   public String id = "";
   public class_2487 tag;

   public PacketHUDScene() {
   }

   public PacketHUDScene(String id, class_2487 tag) {
      this.id = id == null ? "" : id;
      this.tag = tag;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      this.tag = NBTUtils.readInfiniteTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.id == null ? "" : this.id);
      ForgeByteBufUtils.writeTag(buf, this.tag);
   }
}
