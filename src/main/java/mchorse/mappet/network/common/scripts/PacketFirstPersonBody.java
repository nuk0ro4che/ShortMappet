package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketFirstPersonBody implements IMessage {
   public boolean enabled;

   public PacketFirstPersonBody() {
   }

   public PacketFirstPersonBody(boolean enabled) {
      this.enabled = enabled;
   }

   public void fromBytes(ByteBuf buf) {
      this.enabled = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.enabled);
   }
}
