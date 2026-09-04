package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketMouseSensitivity implements IMessage {
   public static final byte REQUEST = 0;
   public static final byte RESPONSE = 1;

   public byte action;
   public double value;

   public PacketMouseSensitivity() {
   }

   public PacketMouseSensitivity(byte action) {
      this(action, 0.5D);
   }

   public PacketMouseSensitivity(byte action, double value) {
      this.action = action;
      this.value = value;
   }

   public void fromBytes(ByteBuf buf) {
      this.action = buf.readByte();
      this.value = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.action);
      buf.writeDouble(this.value);
   }
}
