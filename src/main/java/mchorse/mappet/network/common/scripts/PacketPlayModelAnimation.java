package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketPlayModelAnimation implements IMessage {
   private static final int MAX_ANIMATION_LENGTH = 256;

   public String animation = "";
   public int entityId;

   public PacketPlayModelAnimation() {
   }

   public PacketPlayModelAnimation(String animation, int entityId) {
      this.animation = sanitize(animation);
      this.entityId = entityId;
   }

   public void fromBytes(ByteBuf buf) {
      this.animation = sanitize(ForgeByteBufUtils.readUTF8String(buf));
      this.entityId = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, sanitize(this.animation));
      buf.writeInt(this.entityId);
   }

   private static String sanitize(String animation) {
      if (animation == null) {
         return "";
      }

      return animation.length() > MAX_ANIMATION_LENGTH ? animation.substring(0, MAX_ANIMATION_LENGTH) : animation;
   }
}
