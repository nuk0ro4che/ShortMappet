package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketHudScale implements IMessage {
   public String mod;
   public float scale;

   public PacketHudScale() {
   }

   public PacketHudScale(String mod, float scale) {
      this.mod = mod;
      this.scale = scale;
   }

   public void fromBytes(ByteBuf buf) {
      this.mod = ForgeByteBufUtils.readUTF8String(buf);
      this.scale = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.mod);
      buf.writeFloat(this.scale);
   }
}