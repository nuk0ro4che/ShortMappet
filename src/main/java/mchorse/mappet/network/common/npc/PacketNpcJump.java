package mchorse.mappet.network.common.npc;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.IMessage;

public class PacketNpcJump implements IMessage {
   public int entityId;
   public float jumpPower;

   public PacketNpcJump() {
   }

   public PacketNpcJump(int entityId, float jumpPower) {
      this.entityId = entityId;
      this.jumpPower = jumpPower;
   }

   public float getJumpPower() {
      return this.jumpPower;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityId);
      buf.writeFloat(this.jumpPower);
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = buf.readInt();
      this.jumpPower = buf.readFloat();
   }
}
