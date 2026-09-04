package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketKeyBinding implements IMessage {
   public static final byte SET = 0;
   public static final byte RESET = 1;
   public static final byte REQUEST = 2;
   public static final byte RESPONSE = 3;
   public static final byte ACTIVATE = 4;

   public byte action;
   public String id = "";
   public String key = "";

   public PacketKeyBinding() {
   }

   public PacketKeyBinding(byte action, String id, String key) {
      this.action = action;
      this.id = id == null ? "" : id;
      this.key = key == null ? "key.keyboard.unknown" : key;
   }

   public void fromBytes(ByteBuf buf) {
      this.action = buf.readByte();
      this.id = ForgeByteBufUtils.readUTF8String(buf);
      this.key = ForgeByteBufUtils.readUTF8String(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.action);
      ForgeByteBufUtils.writeUTF8String(buf, this.id);
      ForgeByteBufUtils.writeUTF8String(buf, this.key);
   }
}
