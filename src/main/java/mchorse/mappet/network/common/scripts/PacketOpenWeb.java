package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketOpenWeb implements IMessage {
   private static final int MAX_URL_LENGTH = 2048;
   public String url = "";

   public PacketOpenWeb() {
   }

   public PacketOpenWeb(String url) {
      this.url = sanitize(url);
   }

   public void fromBytes(ByteBuf buf) {
      this.url = sanitize(ForgeByteBufUtils.readUTF8String(buf));
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, sanitize(this.url));
   }

   public static String sanitize(String url) {
      if (url == null) {
         return "";
      }

      return url.length() > MAX_URL_LENGTH ? url.substring(0, MAX_URL_LENGTH) : url;
   }
}
