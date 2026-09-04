package mchorse.mappet.network.common.ui;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;





public class PacketCloseUI implements IMessage {
   public boolean closeMappet;

   public PacketCloseUI() {
      this(false);
   }

   public PacketCloseUI(boolean closeMappet) {
      this.closeMappet = closeMappet;
   }

   public void fromBytes(ByteBuf buf) {
      this.closeMappet = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.closeMappet);
   }
}
