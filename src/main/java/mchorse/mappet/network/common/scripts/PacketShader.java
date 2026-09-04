package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.shaders.ShaderFile;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;


public class PacketShader implements IMessage {
   public boolean remove;
   public int target;
   public ShaderFile shader;

   public PacketShader() {
   }

   private PacketShader(boolean remove, int target, ShaderFile shader) {
      this.remove = remove;
      this.target = target;
      this.shader = shader;
   }

   public static PacketShader apply(int target, ShaderFile shader) {
      return new PacketShader(false, target, shader);
   }

   public static PacketShader remove(int target) {
      return new PacketShader(true, target, null);
   }

   public void fromBytes(ByteBuf buf) {
      this.remove = buf.readBoolean();
      this.target = buf.readUnsignedByte();
      if (!this.remove) {
         this.shader = new ShaderFile();
         this.shader.setId(ForgeByteBufUtils.readUTF8String(buf));
         this.shader.deserializeNBT(ForgeByteBufUtils.readTag(buf));
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.remove);
      buf.writeByte(this.target);
      if (!this.remove) {
         ForgeByteBufUtils.writeUTF8String(buf, this.shader == null ? "" : this.shader.getId());
         ForgeByteBufUtils.writeTag(buf, this.shader == null ? null : this.shader.serializeNBT());
      }
   }
}
