package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketClientSetting implements IMessage {
   public String setting = "";
   public double value;

   public PacketClientSetting() {
   }

   public PacketClientSetting(String setting, double value) {
      this.setting = setting == null ? "" : setting;
      this.value = value;
   }

   public void fromBytes(ByteBuf buf) {
      this.setting = ForgeByteBufUtils.readUTF8String(buf);
      this.value = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.setting);
      buf.writeDouble(this.value);
   }
}
