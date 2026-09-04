package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;


public class PacketMovementLock implements IMessage {
   public static final byte JUMP = 0;
   public static final byte SPRINT = 1;

   public byte action;
   public boolean disabled;

   public PacketMovementLock() {
   }

   public PacketMovementLock(byte action, boolean disabled) {
      this.action = action;
      this.disabled = disabled;
   }

   public void fromBytes(ByteBuf buf) {
      this.action = buf.readByte();
      this.disabled = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.action);
      buf.writeBoolean(this.disabled);
   }
}
