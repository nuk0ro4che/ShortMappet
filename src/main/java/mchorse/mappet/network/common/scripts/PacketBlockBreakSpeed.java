package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;


public class PacketBlockBreakSpeed implements IMessage {
   public float multiplier;
   public boolean reset;

   public PacketBlockBreakSpeed() {
   }

   public PacketBlockBreakSpeed(float multiplier, boolean reset) {
      this.multiplier = multiplier;
      this.reset = reset;
   }

   public void fromBytes(ByteBuf buf) {
      this.multiplier = buf.readFloat();
      this.reset = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeFloat(this.multiplier);
      buf.writeBoolean(this.reset);
   }
}