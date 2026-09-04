package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketHudVisibility implements IMessage {
   public byte element;
   public boolean visible;

   public PacketHudVisibility() {
   }

   public PacketHudVisibility(int element, boolean visible) {
      this.element = (byte)element;
      this.visible = visible;
   }

   public void fromBytes(ByteBuf buf) {
      this.element = buf.readByte();
      this.visible = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.element);
      buf.writeBoolean(this.visible);
   }
}
