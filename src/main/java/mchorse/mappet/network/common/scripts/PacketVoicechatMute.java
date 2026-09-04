package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import mchorse.mclib.network.IMessage;


public class PacketVoicechatMute implements IMessage {
   public UUID player;
   public boolean muted;

   public PacketVoicechatMute() {
   }

   public PacketVoicechatMute(UUID player, boolean muted) {
      this.player = player;
      this.muted = muted;
   }

   public void fromBytes(ByteBuf buf) {
      this.player = new UUID(buf.readLong(), buf.readLong());
      this.muted = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.player.getMostSignificantBits());
      buf.writeLong(this.player.getLeastSignificantBits());
      buf.writeBoolean(this.muted);
   }
}
