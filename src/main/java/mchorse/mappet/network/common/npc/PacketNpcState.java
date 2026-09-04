package mchorse.mappet.network.common.npc;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketNpcState implements IMessage {
   public int entityId;
   public class_2487 state;

   public PacketNpcState() {
   }

   public PacketNpcState(int entityId, class_2487 state) {
      this.entityId = entityId;
      this.state = state;
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = buf.readInt();
      this.state = NBTUtils.readInfiniteTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityId);
      ForgeByteBufUtils.writeTag(buf, this.state);
   }
}
