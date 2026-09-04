package mchorse.mappet.network.common.huds;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketHUDMorph implements IMessage {
   public String id = "";
   public int index;
   public class_2487 morph;

   public PacketHUDMorph() {
   }

   public PacketHUDMorph(String id, int index, class_2487 morph) {
      this.id = id;
      this.index = index;
      this.morph = morph;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      this.index = buf.readInt();
      this.morph = NBTUtils.readInfiniteTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.id);
      buf.writeInt(this.index);
      ForgeByteBufUtils.writeTag(buf, this.morph);
   }
}
