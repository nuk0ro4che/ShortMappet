package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketSound implements IMessage {
   public String sound = "";
   public String soundCategory = "";
   public float volume = 1.0F;
   public float pitch = 1.0F;
   public boolean loop;
   public boolean stopLoop;

   public PacketSound() {
   }

   public PacketSound(String sound, String soundCategory, float volume, float pitch) {
      this.sound = sound;
      this.soundCategory = soundCategory;
      this.volume = volume;
      this.pitch = pitch;
   }

   public static PacketSound loop(String sound, String category, float volume, float pitch) {
      PacketSound packet = new PacketSound(sound, category, volume, pitch);
      packet.loop = true;
      return packet;
   }

   public static PacketSound stopLoop(String sound, String category) {
      PacketSound packet = new PacketSound(sound, category, 0.0F, 0.0F);
      packet.stopLoop = true;
      return packet;
   }

   public void fromBytes(ByteBuf buf) {
      this.sound = ForgeByteBufUtils.readUTF8String(buf);
      this.soundCategory = ForgeByteBufUtils.readUTF8String(buf);
      this.volume = buf.readFloat();
      this.pitch = buf.readFloat();
      this.loop = buf.readBoolean();
      this.stopLoop = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.sound);
      ForgeByteBufUtils.writeUTF8String(buf, this.soundCategory);
      buf.writeFloat(this.volume);
      buf.writeFloat(this.pitch);
      buf.writeBoolean(this.loop);
      buf.writeBoolean(this.stopLoop);
   }
}
