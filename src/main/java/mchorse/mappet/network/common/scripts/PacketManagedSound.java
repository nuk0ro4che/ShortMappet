package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;


public class PacketManagedSound implements IMessage {
   public static final byte PLAY = 0;
   public static final byte UPDATE = 1;
   public static final byte STOP = 2;
   public static final byte SET_TIME_CODE = 3;
   public static final byte REQUEST_TIME_CODE = 4;
   public static final byte TIME_CODE = 5;
   public static final byte FINISHED = 6;
   public byte action;
   public String id = "";
   public String name = "";
   public String category = "master";
   public boolean staticSound;
   public double x;
   public double y;
   public double z;
   public float volume = 1.0F;
   public float pitch = 1.0F;
   public double timeCode;
   public boolean entityBound;
   public int entityId = -1;

   public PacketManagedSound() {
   }

   public static PacketManagedSound play(String id, String name, String category, boolean staticSound, double x, double y, double z, float volume, float pitch) {
      PacketManagedSound packet = new PacketManagedSound();
      packet.action = PLAY;
      packet.id = id;
      packet.name = name;
      packet.category = category;
      packet.staticSound = staticSound;
      packet.x = x;
      packet.y = y;
      packet.z = z;
      packet.volume = volume;
      packet.pitch = pitch;
      return packet;
   }

   public static PacketManagedSound playEntity(String id, String name, String category, boolean staticSound, int entityId, double x, double y, double z, float volume, float pitch) {
      PacketManagedSound packet = play(id, name, category, staticSound, x, y, z, volume, pitch);
      packet.entityBound = true;
      packet.entityId = entityId;
      return packet;
   }

   public static PacketManagedSound update(String id, double x, double y, double z, float volume) {
      PacketManagedSound packet = new PacketManagedSound();
      packet.action = UPDATE;
      packet.id = id;
      packet.x = x;
      packet.y = y;
      packet.z = z;
      packet.volume = volume;
      return packet;
   }

   public static PacketManagedSound stop(String id) {
      PacketManagedSound packet = new PacketManagedSound();
      packet.action = STOP;
      packet.id = id;
      return packet;
   }

   public static PacketManagedSound setTimeCode(String id, double timeCode) {
      PacketManagedSound packet = new PacketManagedSound();
      packet.action = SET_TIME_CODE;
      packet.id = id;
      packet.timeCode = timeCode;
      return packet;
   }

   public static PacketManagedSound requestTimeCode(String id) {
      PacketManagedSound packet = new PacketManagedSound();
      packet.action = REQUEST_TIME_CODE;
      packet.id = id;
      return packet;
   }

   public static PacketManagedSound timeCode(String id, double timeCode) {
      PacketManagedSound packet = new PacketManagedSound();
      packet.action = TIME_CODE;
      packet.id = id;
      packet.timeCode = timeCode;
      return packet;
   }

   public static PacketManagedSound finished(String id, String name) {
      PacketManagedSound packet = new PacketManagedSound();
      packet.action = FINISHED;
      packet.id = id;
      packet.name = name;
      return packet;
   }

   public void fromBytes(ByteBuf buf) {
      this.action = buf.readByte();
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      this.name = ForgeByteBufUtils.readUTF8String(buf);
      this.category = ForgeByteBufUtils.readUTF8String(buf);
      this.staticSound = buf.readBoolean();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.volume = buf.readFloat();
      this.pitch = buf.readFloat();
      this.timeCode = buf.readDouble();
      this.entityBound = buf.readBoolean();
      this.entityId = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.action);
      ForgeByteBufUtils.writeUTF8String(buf, this.id == null ? "" : this.id);
      ForgeByteBufUtils.writeUTF8String(buf, this.name == null ? "" : this.name);
      ForgeByteBufUtils.writeUTF8String(buf, this.category == null ? "master" : this.category);
      buf.writeBoolean(this.staticSound);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.volume);
      buf.writeFloat(this.pitch);
      buf.writeDouble(this.timeCode);
      buf.writeBoolean(this.entityBound);
      buf.writeInt(this.entityId);
   }
}
