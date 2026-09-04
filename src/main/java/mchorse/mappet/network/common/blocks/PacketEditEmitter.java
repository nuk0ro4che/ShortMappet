package mchorse.mappet.network.common.blocks;

import io.netty.buffer.ByteBuf;
import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.tile.TileEmitter;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2338;
import net.minecraft.class_2487;
import net.minecraft.class_2520;

public class PacketEditEmitter implements IMessage {
   public class_2338 pos;
   public class_2487 checker;
   public float radius;
   public int update;
   public boolean disable;

   public PacketEditEmitter() {
   }

   public PacketEditEmitter(TileEmitter tile) {
      this(tile.method_11016(), tile.getChecker().toNBT(), tile.getRadius(), tile.getUpdate(), tile.getDisable());
   }

   public PacketEditEmitter(class_2338 pos, class_2487 checker, float radius, int update, boolean disable) {
      this.pos = pos;
      this.checker = checker;
      this.radius = radius;
      this.update = update;
      this.disable = disable;
   }

   public Checker createChecker() {
      Checker checker = new Checker();
      checker.deserializeNBT((class_2520)this.checker);
      return checker;
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = new class_2338(buf.readInt(), buf.readInt(), buf.readInt());
      this.checker = NBTUtils.readInfiniteTag(buf);
      this.radius = buf.readFloat();
      this.update = buf.readInt();
      this.disable = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.pos.method_10263());
      buf.writeInt(this.pos.method_10264());
      buf.writeInt(this.pos.method_10260());
      ForgeByteBufUtils.writeTag(buf, this.checker);
      buf.writeFloat(this.radius);
      buf.writeInt(this.update);
      buf.writeBoolean(this.disable);
   }
}
