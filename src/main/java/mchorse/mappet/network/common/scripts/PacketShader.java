package mchorse.mappet.network.common.scripts;

import java.util.LinkedHashMap;
import java.util.Map;
import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.shaders.ShaderParameters;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;


public class PacketShader implements IMessage {
   public boolean remove;
   public int target;
   public String id;
   public Map<String, Object> parameters = new LinkedHashMap<String, Object>();

   public PacketShader() {
   }

   private PacketShader(boolean remove, int target, String id, Map<String, Object> parameters) {
      this.remove = remove;
      this.target = target;
      this.id = id;
      this.parameters = ShaderParameters.normalize(parameters);
   }

   public static PacketShader apply(int target, String id) {
      return apply(target, id, null);
   }

   public static PacketShader apply(int target, String id, Map<String, Object> parameters) {
      return new PacketShader(false, target, id, parameters);
   }

   public static PacketShader remove(int target) {
      return new PacketShader(true, target, "", null);
   }

   public static PacketShader remove(int target, String id) {
      return new PacketShader(true, target, id, null);
   }

   public void fromBytes(ByteBuf buf) {
      this.remove = buf.readBoolean();
      this.target = buf.readUnsignedByte();
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      if (this.id == null) {
         this.id = "";
      }
      this.parameters = ShaderParameters.fromNbt(NBTUtils.readInfiniteTag(buf));
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.remove);
      buf.writeByte(this.target);
      ForgeByteBufUtils.writeUTF8String(buf, this.id == null ? "" : this.id);
      ForgeByteBufUtils.writeTag(buf, ShaderParameters.toNbt(this.parameters));
   }
}
