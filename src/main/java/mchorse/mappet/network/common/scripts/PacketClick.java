package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;
import net.minecraft.class_1268;

public class PacketClick implements IMessage {
   public class_1268 hand;

   public PacketClick() {
      this.hand = class_1268.field_5808;
   }

   public PacketClick(class_1268 hand) {
      this.hand = class_1268.field_5808;
      this.hand = hand;
   }

   public void fromBytes(ByteBuf buf) {
      this.hand = class_1268.values()[buf.readInt()];
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.hand.ordinal());
   }
}
