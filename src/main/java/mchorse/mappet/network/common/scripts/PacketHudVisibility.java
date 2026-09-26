package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketHudVisibility implements IMessage {
   public byte element;
   public String mod;
   public boolean visible;

   public PacketHudVisibility() {
   }

   public PacketHudVisibility(int element, boolean visible) {
      this.element = (byte)element;
      this.visible = visible;
   }

   public PacketHudVisibility(String mod, boolean visible) {
      this.mod = mod;
      this.visible = visible;
   }

   public void fromBytes(ByteBuf buf) {
      if (buf.readBoolean()) {
         this.mod = ForgeByteBufUtils.readUTF8String(buf);
      } else {
         this.element = buf.readByte();
      }

      this.visible = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.mod != null);
      if (this.mod != null) {
         ForgeByteBufUtils.writeUTF8String(buf, this.mod);
      } else {
         buf.writeByte(this.element);
      }

      buf.writeBoolean(this.visible);
   }
}