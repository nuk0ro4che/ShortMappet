package mchorse.mappet.network.common.blocks;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.blocks.BlockTrigger;
import mchorse.mappet.tile.TileTrigger;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2487;

public class PacketEditTrigger implements IMessage {
   public class_2338 pos;
   public class_2487 left;
   public class_2487 right;
   public boolean collidable;
   public class_243 boundingBoxPos1;
   public class_243 boundingBoxPos2;

   public PacketEditTrigger() {
      this.left = new class_2487();
      this.right = new class_2487();
   }

   public PacketEditTrigger(TileTrigger tile) {
      this(tile.method_11016(), tile.leftClick.serializeNBT(), tile.rightClick.serializeNBT(), (Boolean)tile.method_10997().method_8320(tile.method_11016()).method_11654(BlockTrigger.COLLIDABLE), new class_243(tile.boundingBoxPos1.field_1352, tile.boundingBoxPos1.field_1351, tile.boundingBoxPos1.field_1350), new class_243(tile.boundingBoxPos2.field_1352, tile.boundingBoxPos2.field_1351, tile.boundingBoxPos2.field_1350));
   }

   public PacketEditTrigger(class_2338 pos, class_2487 left, class_2487 right, boolean collidable, class_243 boundingBoxPos1, class_243 boundingBoxPos2) {
      this.left = new class_2487();
      this.right = new class_2487();
      this.pos = pos;
      this.left = left;
      this.right = right;
      this.collidable = collidable;
      this.boundingBoxPos1 = boundingBoxPos1;
      this.boundingBoxPos2 = boundingBoxPos2;
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = new class_2338(buf.readInt(), buf.readInt(), buf.readInt());
      this.left = NBTUtils.readInfiniteTag(buf);
      this.right = NBTUtils.readInfiniteTag(buf);
      this.collidable = buf.readBoolean();
      this.boundingBoxPos1 = new class_243(buf.readDouble(), buf.readDouble(), buf.readDouble());
      this.boundingBoxPos2 = new class_243(buf.readDouble(), buf.readDouble(), buf.readDouble());
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.pos.method_10263());
      buf.writeInt(this.pos.method_10264());
      buf.writeInt(this.pos.method_10260());
      ForgeByteBufUtils.writeTag(buf, this.left);
      ForgeByteBufUtils.writeTag(buf, this.right);
      buf.writeBoolean(this.collidable);
      buf.writeDouble(this.boundingBoxPos1.field_1352);
      buf.writeDouble(this.boundingBoxPos1.field_1351);
      buf.writeDouble(this.boundingBoxPos1.field_1350);
      buf.writeDouble(this.boundingBoxPos2.field_1352);
      buf.writeDouble(this.boundingBoxPos2.field_1351);
      buf.writeDouble(this.boundingBoxPos2.field_1350);
   }
}
