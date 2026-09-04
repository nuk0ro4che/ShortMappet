package mchorse.mappet.network.common.blocks;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2338;
import net.minecraft.class_2487;

public class PacketEditConditionModel implements IMessage {
   public class_2338 pos;
   public boolean isEdit;
   public class_2487 tag;

   public PacketEditConditionModel() {
   }

   public PacketEditConditionModel(class_2338 pos, class_2487 tag) {
      this.isEdit = true;
      this.pos = pos;
      this.tag = tag;
   }

   public PacketEditConditionModel setIsEdit(boolean isEdit) {
      this.isEdit = isEdit;
      return this;
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = new class_2338(buf.readInt(), buf.readInt(), buf.readInt());
      this.isEdit = buf.readBoolean();
      this.tag = NBTUtils.readInfiniteTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.pos.method_10263());
      buf.writeInt(this.pos.method_10264());
      buf.writeInt(this.pos.method_10260());
      buf.writeBoolean(this.isEdit);
      ForgeByteBufUtils.writeTag(buf, this.tag);
   }
}
