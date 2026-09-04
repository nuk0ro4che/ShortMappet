package mchorse.mappet.network.common.ui;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketUIData implements IMessage {
   public class_2487 data;

   public PacketUIData() {
   }

   public PacketUIData(class_2487 data) {
      this.data = data;
   }

   public void fromBytes(ByteBuf buf) {
      this.data = NBTUtils.readInfiniteTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeTag(buf, this.data);
   }
}
