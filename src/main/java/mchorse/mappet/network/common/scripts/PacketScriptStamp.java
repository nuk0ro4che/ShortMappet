package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;


/** Timestamp of a script file on the server side, used to detect changes made outside of the editor */
public class PacketScriptStamp implements IMessage {
   private static final int MAX_NAME_LENGTH = 1024;
   public String script = "";
   public long modified;
   public long length;
   public long dataModified;

   public PacketScriptStamp() {
   }

   public PacketScriptStamp(String script, long modified, long length, long dataModified) {
      this.script = limit(script);
      this.modified = modified;
      this.length = length;
      this.dataModified = dataModified;
   }

   public void fromBytes(ByteBuf buf) {
      this.script = limit(ForgeByteBufUtils.readUTF8String(buf));
      this.modified = buf.readLong();
      this.length = buf.readLong();
      this.dataModified = buf.readLong();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, limit(this.script));
      buf.writeLong(this.modified);
      buf.writeLong(this.length);
      buf.writeLong(this.dataModified);
   }

   private static String limit(String value) {
      if (value == null) {
         return "";
      }

      return value.length() > MAX_NAME_LENGTH ? value.substring(0, MAX_NAME_LENGTH) : value;
   }
}
