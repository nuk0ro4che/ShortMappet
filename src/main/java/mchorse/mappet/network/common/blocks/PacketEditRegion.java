package mchorse.mappet.network.common.blocks;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.tile.TileRegion;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2338;
import net.minecraft.class_2487;

public class PacketEditRegion implements IMessage {
   public boolean open;
   public class_2338 pos;
   public class_2487 tag;

   public PacketEditRegion() {
   }

   public PacketEditRegion(TileRegion tile) {
      this(tile.method_11016(), tile.region.serializeNBT());
   }

   public PacketEditRegion(class_2338 pos, class_2487 tag) {
      this.pos = pos;
      this.tag = tag;
   }

   public PacketEditRegion open() {
      this.open = true;
      return this;
   }

   public void fromBytes(ByteBuf buf) {
      this.open = buf.readBoolean();
      this.pos = new class_2338(buf.readInt(), buf.readInt(), buf.readInt());
      this.tag = NBTUtils.readInfiniteTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.open);
      buf.writeInt(this.pos.method_10263());
      buf.writeInt(this.pos.method_10264());
      buf.writeInt(this.pos.method_10260());
      ForgeByteBufUtils.writeTag(buf, this.tag);
   }
}
