package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketCameraShake implements IMessage {
   public boolean active;
   public boolean restart;
   public int ticks;
   public float pitch;
   public float yaw;
   public float roll;
   public float frequency;
   public int fadeTicks;

   public PacketCameraShake() {
   }

   public PacketCameraShake(boolean active, boolean restart, int ticks, double pitch, double yaw, double roll, double frequency, int fadeTicks) {
      this.active = active;
      this.restart = restart;
      this.ticks = ticks;
      this.pitch = (float)pitch;
      this.yaw = (float)yaw;
      this.roll = (float)roll;
      this.frequency = (float)frequency;
      this.fadeTicks = fadeTicks;
   }

   public void fromBytes(ByteBuf buf) {
      this.active = buf.readBoolean();
      this.restart = buf.readBoolean();
      this.ticks = buf.readInt();
      this.pitch = buf.readFloat();
      this.yaw = buf.readFloat();
      this.roll = buf.readFloat();
      this.frequency = buf.readFloat();
      this.fadeTicks = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.active);
      buf.writeBoolean(this.restart);
      buf.writeInt(this.ticks);
      buf.writeFloat(this.pitch);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.roll);
      buf.writeFloat(this.frequency);
      buf.writeInt(this.fadeTicks);
   }
}
