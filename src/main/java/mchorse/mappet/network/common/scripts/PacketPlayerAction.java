package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketPlayerAction implements IMessage {
   public static final int QUIT_MINECRAFT = 0;
   public static final int QUIT_WORLD = 1;
   public static final int OPEN_SETTINGS = 2;
   public int action;

   public PacketPlayerAction() {
   }

   public PacketPlayerAction(int action) {
      this.action = action;
   }

   public void fromBytes(ByteBuf buf) {
      this.action = buf.readByte();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.action);
   }
}
