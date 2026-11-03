package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;


public class PacketSprintHunger implements IMessage {
   public boolean disabled;

   public PacketSprintHunger() {
   }

   public PacketSprintHunger(boolean disabled) {
      this.disabled = disabled;
   }

   public void fromBytes(ByteBuf buf) {
      this.disabled = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.disabled);
   }
}
