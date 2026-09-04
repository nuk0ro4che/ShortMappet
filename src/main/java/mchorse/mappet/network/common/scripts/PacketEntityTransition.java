package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;
import mchorse.mclib.network.IMessage;






public class PacketEntityTransition implements IMessage {
   public int entityId;
   public boolean position;
   public boolean rotation;
   public boolean relativeRotation;
   public int durationTicks;
   public String interpolation = "LINEAR";
   public double x;
   public double y;
   public double z;
   public float pitch;
   public float yaw;
   public float yawHead;

   public PacketEntityTransition() {
   }

   public PacketEntityTransition(int entityId, boolean position, boolean rotation, String interpolation, int durationTicks, double x, double y, double z, float pitch, float yaw, float yawHead) {
      this.entityId = entityId;
      this.position = position;
      this.rotation = rotation;
      this.interpolation = interpolation == null ? "LINEAR" : interpolation;
      this.durationTicks = Math.max(0, durationTicks);
      this.x = x;
      this.y = y;
      this.z = z;
      this.pitch = pitch;
      this.yaw = yaw;
      this.yawHead = yawHead;
   }

   public PacketEntityTransition(int entityId, boolean position, boolean rotation, String interpolation, int durationTicks, double x, double y, double z, float pitch, float yaw, float yawHead, boolean relativeRotation) {
      this(entityId, position, rotation, interpolation, durationTicks, x, y, z, pitch, yaw, yawHead);
      this.relativeRotation = relativeRotation;
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = buf.readInt();
      this.position = buf.readBoolean();
      this.rotation = buf.readBoolean();
      this.relativeRotation = buf.readBoolean();
      this.durationTicks = buf.readInt();
      int length = Math.max(0, Math.min(64, buf.readInt()));
      byte[] bytes = new byte[length];
      buf.readBytes(bytes);
      this.interpolation = new String(bytes, StandardCharsets.UTF_8);
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.pitch = buf.readFloat();
      this.yaw = buf.readFloat();
      this.yawHead = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeBoolean(this.position);
      buf.writeBoolean(this.rotation);
      buf.writeBoolean(this.relativeRotation);
      buf.writeInt(this.durationTicks);
      byte[] bytes = (this.interpolation == null ? "LINEAR" : this.interpolation).getBytes(StandardCharsets.UTF_8);
      buf.writeInt(bytes.length);
      buf.writeBytes(bytes);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.pitch);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.yawHead);
   }
}
