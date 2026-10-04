package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;


public class PacketShader implements IMessage {
   public boolean remove;
   public int target;
   public String id;

   public PacketShader() {
   }

   private PacketShader(boolean remove, int target, String id) {
      this.remove = remove;
      this.target = target;
      this.id = id;
   }

   public static PacketShader apply(int target, String id) {
      return new PacketShader(false, target, id);
   }

   public static PacketShader remove(int target) {
      return new PacketShader(true, target, "");
   }

   public static PacketShader remove(int target, String id) {
      return new PacketShader(true, target, id);
   }

   public void fromBytes(ByteBuf buf) {
      this.remove = buf.readBoolean();
      this.target = buf.readUnsignedByte();
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      if (this.id == null) {
         this.id = "";
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.remove);
      buf.writeByte(this.target);
      ForgeByteBufUtils.writeUTF8String(buf, this.id == null ? "" : this.id);
   }
}