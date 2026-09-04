package mchorse.mappet.network.common.utils;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;
import net.minecraft.class_2338;
import net.minecraft.class_243;

public class PacketChangedBoundingBox implements IMessage {
   public class_2338 pos;
   public class_243 boundingBoxPos1;
   public class_243 boundingBoxPos2;

   public PacketChangedBoundingBox() {
   }

   public PacketChangedBoundingBox(class_2338 pos, class_243 boundingBoxPos1, class_243 boundingBoxPos2) {
      this.pos = pos;
      this.boundingBoxPos1 = boundingBoxPos1;
      this.boundingBoxPos2 = boundingBoxPos2;
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = new class_2338(buf.readInt(), buf.readInt(), buf.readInt());
      this.boundingBoxPos1 = new class_243(buf.readDouble(), buf.readDouble(), buf.readDouble());
      this.boundingBoxPos2 = new class_243(buf.readDouble(), buf.readDouble(), buf.readDouble());
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.pos.method_10263());
      buf.writeInt(this.pos.method_10264());
      buf.writeInt(this.pos.method_10260());
      buf.writeDouble(this.boundingBoxPos1.field_1352);
      buf.writeDouble(this.boundingBoxPos1.field_1351);
      buf.writeDouble(this.boundingBoxPos1.field_1350);
      buf.writeDouble(this.boundingBoxPos2.field_1352);
      buf.writeDouble(this.boundingBoxPos2.field_1351);
      buf.writeDouble(this.boundingBoxPos2.field_1350);
   }
}
