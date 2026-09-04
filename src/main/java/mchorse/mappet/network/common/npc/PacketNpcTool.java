package mchorse.mappet.network.common.npc;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketNpcTool implements IMessage {
   public String npc = "";
   public String state = "";

   public PacketNpcTool() {
   }

   public PacketNpcTool(String npc, String state) {
      this.npc = npc;
      this.state = state;
   }

   public void fromBytes(ByteBuf buf) {
      this.npc = ForgeByteBufUtils.readUTF8String(buf);
      this.state = ForgeByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, this.npc);
      ForgeByteBufUtils.writeUTF8String(buf, this.state);
   }
}
