package mchorse.mappet.network.common.items;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketScriptedItemInfo implements IMessage {
   public class_2487 tag;
   public class_2487 stackTag;
   public int entity;

   public PacketScriptedItemInfo() {
      this.tag = new class_2487();
   }

   public PacketScriptedItemInfo(class_2487 tag, class_2487 stackTag, int entity) {
      this.tag = tag;
      this.stackTag = stackTag;
      this.entity = entity;
   }

   public void fromBytes(ByteBuf buf) {
      this.tag = NBTUtils.readInfiniteTag(buf);
      if (buf.readBoolean()) {
         this.stackTag = NBTUtils.readInfiniteTag(buf);
      }

      this.entity = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeTag(buf, this.tag);
      buf.writeBoolean(this.stackTag != null);
      if (this.stackTag != null) {
         ForgeByteBufUtils.writeTag(buf, this.stackTag);
      }

      buf.writeInt(this.entity);
   }
}
