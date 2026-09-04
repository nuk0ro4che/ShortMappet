package mchorse.mappet.network.common.crafting;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketCraft implements IMessage {
   public int index;

   public PacketCraft() {
   }

   public PacketCraft(int index) {
      this.index = index;
   }

   public void fromBytes(ByteBuf buf) {
      this.index = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.index);
   }
}
