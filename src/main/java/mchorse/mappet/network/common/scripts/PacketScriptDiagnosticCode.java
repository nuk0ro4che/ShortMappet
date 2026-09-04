package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;


public class PacketScriptDiagnosticCode implements IMessage {
   private static final int MAX_NAME_LENGTH = 1024;
   private static final int MAX_CODE_LENGTH = 262144;
   private static final int MAX_LIBRARY_FUNCTIONS = 4096;
   private static final int MAX_FUNCTION_NAME_LENGTH = 256;
   public String script = "";
   public String code = "";
   public boolean clientScript;
   public boolean library;
   public final List<String> libraryFunctions = new ArrayList();

   public PacketScriptDiagnosticCode() {
   }

   public PacketScriptDiagnosticCode(String script, String code, Collection<String> libraryFunctions) {
      this(script, code, libraryFunctions, false);
   }

   public PacketScriptDiagnosticCode(String script, String code, Collection<String> libraryFunctions, boolean clientScript) {
      this(script, code, libraryFunctions, clientScript, false);
   }

   public PacketScriptDiagnosticCode(String script, String code, Collection<String> libraryFunctions, boolean clientScript, boolean library) {
      this.script = limitName(script);
      this.code = limitCode(code);
      this.clientScript = clientScript;
      this.library = library;
      if (libraryFunctions != null) {
         int count = 0;

         for(String name : libraryFunctions) {
            if (count++ >= MAX_LIBRARY_FUNCTIONS) {
               break;
            }

            this.libraryFunctions.add(limitFunctionName(name));
         }
      }
   }

   public void fromBytes(ByteBuf buf) {
      this.script = limitName(ForgeByteBufUtils.readUTF8String(buf));
      this.code = limitCode(ForgeByteBufUtils.readUTF8String(buf));
      this.clientScript = buf.readBoolean();
      this.library = buf.readBoolean();
      this.libraryFunctions.clear();
      int count = Math.min(Math.max(buf.readInt(), 0), MAX_LIBRARY_FUNCTIONS);

      for(int i = 0; i < count; ++i) {
         this.libraryFunctions.add(limitFunctionName(ForgeByteBufUtils.readUTF8String(buf)));
      }
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, limitName(this.script));
      ForgeByteBufUtils.writeUTF8String(buf, limitCode(this.code));
      buf.writeBoolean(this.clientScript);
      buf.writeBoolean(this.library);
      int count = Math.min(this.libraryFunctions.size(), MAX_LIBRARY_FUNCTIONS);
      buf.writeInt(count);

      for(int i = 0; i < count; ++i) {
         ForgeByteBufUtils.writeUTF8String(buf, limitFunctionName((String)this.libraryFunctions.get(i)));
      }
   }

   private static String limitName(String value) {
      if (value == null) {
         return "";
      }

      return value.length() > MAX_NAME_LENGTH ? value.substring(0, MAX_NAME_LENGTH) : value;
   }

   private static String limitCode(String value) {
      if (value == null) {
         return "";
      }

      return value.length() > MAX_CODE_LENGTH ? value.substring(0, MAX_CODE_LENGTH) : value;
   }

   private static String limitFunctionName(String value) {
      if (value == null) {
         return "";
      }

      return value.length() > MAX_FUNCTION_NAME_LENGTH ? value.substring(0, MAX_FUNCTION_NAME_LENGTH) : value;
   }
}
