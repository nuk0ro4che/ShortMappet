package mchorse.mappet.network.common.content;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketServerSettings implements IMessage {
   public class_2487 tag;

   public PacketServerSettings() {
   }

   public PacketServerSettings(class_2487 tag) {
      this.tag = tag;
   }

   public void fromBytes(ByteBuf buf) {
      this.tag = NBTUtils.readInfiniteTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeTag(buf, this.tag);
   }
}
