package mchorse.mappet.network.common.content;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketStates implements IMessage {
   public String target;
   public class_2487 states;

   public PacketStates() {
   }

   public PacketStates(String target, class_2487 states) {
      this.target = target;
      this.states = states;
   }

   public void fromBytes(ByteBuf buf) {
      this.target = ForgeByteBufUtils.readUTF8String(buf);
      this.states = NBTUtils.readInfiniteTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.target);
      ForgeByteBufUtils.writeTag(buf, this.states);
   }
}
