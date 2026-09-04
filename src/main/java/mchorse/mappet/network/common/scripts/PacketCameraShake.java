package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketCameraShake implements IMessage {
   public boolean active;
   public int ticks;
   public float pitch;
   public float yaw;
   public float roll;
   public float frequency;

   public PacketCameraShake() {
   }

   public PacketCameraShake(boolean active, int ticks, double pitch, double yaw, double roll, double frequency) {
      this.active = active;
      this.ticks = ticks;
      this.pitch = (float)pitch;
      this.yaw = (float)yaw;
      this.roll = (float)roll;
      this.frequency = (float)frequency;
   }

   public void fromBytes(ByteBuf buf) {
      this.active = buf.readBoolean();
      this.ticks = buf.readInt();
      this.pitch = buf.readFloat();
      this.yaw = buf.readFloat();
      this.roll = buf.readFloat();
      this.frequency = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.active);
      buf.writeInt(this.ticks);
      buf.writeFloat(this.pitch);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.roll);
      buf.writeFloat(this.frequency);
   }
}
