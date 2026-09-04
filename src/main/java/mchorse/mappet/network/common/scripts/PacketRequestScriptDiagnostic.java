package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;


public class PacketRequestScriptDiagnostic implements IMessage {
   private static final int MAX_NAME_LENGTH = 1024;
   public String script = "";
   public boolean clientScript;

   public PacketRequestScriptDiagnostic() {
   }

   public PacketRequestScriptDiagnostic(String script) {
      this(script, false);
   }

   public PacketRequestScriptDiagnostic(String script, boolean clientScript) {
      this.script = limit(script);
      this.clientScript = clientScript;
   }

   public void fromBytes(ByteBuf buf) {
      this.script = limit(ForgeByteBufUtils.readUTF8String(buf));
      this.clientScript = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, limit(this.script));
      buf.writeBoolean(this.clientScript);
   }

   private static String limit(String value) {
      if (value == null) {
         return "";
      }

      return value.length() > MAX_NAME_LENGTH ? value.substring(0, MAX_NAME_LENGTH) : value;
   }
}
