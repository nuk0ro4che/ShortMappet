package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketClipboard implements IMessage {
   public static final byte SET = 0;
   public static final byte REQUEST = 1;
   public static final byte RESPONSE = 2;
   private static final int MAX_LENGTH = 32767;

   public byte action;
   public String text = "";

   public PacketClipboard() {
   }

   public PacketClipboard(byte action) {
      this(action, "");
   }

   public PacketClipboard(byte action, String text) {
      this.action = action;
      this.text = sanitize(text);
   }

   public void fromBytes(ByteBuf buf) {
      this.action = buf.readByte();
      this.text = sanitize(ForgeByteBufUtils.readUTF8String(buf));
   }

   public void toBytes(ByteBuf buf) {
      buf.writeByte(this.action);
      ForgeByteBufUtils.writeUTF8String(buf, sanitize(this.text));
   }

   public static String sanitize(String text) {
      if (text == null) {
         return "";
      }

      return text.length() > MAX_LENGTH ? text.substring(0, MAX_LENGTH) : text;
   }
}
