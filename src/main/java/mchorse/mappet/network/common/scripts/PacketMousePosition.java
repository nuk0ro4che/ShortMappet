package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.network.ForgeByteBufUtils;

public class PacketMousePosition implements IMessage {
   public double x;
   public double y;
   public int duration;
   public String interpolation = "sine_inout";
   public boolean relative;

   public PacketMousePosition() {}

   public PacketMousePosition(double x, double y, int duration, String interpolation) {
      this(x, y, duration, interpolation, false);
   }

   public PacketMousePosition(double x, double y, int duration, String interpolation, boolean relative) {
      this.x = x;
      this.y = y;
      this.duration = Math.max(0, duration);
      this.interpolation = interpolation == null ? "sine_inout" : interpolation;
      this.relative = relative;
   }

   public void fromBytes(ByteBuf buf) {
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.duration = Math.max(0, buf.readInt());
      this.interpolation = ForgeByteBufUtils.readUTF8String(buf);
      this.relative = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeInt(this.duration);
      ForgeByteBufUtils.writeUTF8String(buf, this.interpolation == null ? "sine_inout" : this.interpolation);
      buf.writeBoolean(this.relative);
   }
}
