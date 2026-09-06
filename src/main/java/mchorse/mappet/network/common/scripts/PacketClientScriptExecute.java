package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import java.util.Map;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;


public class PacketClientScriptExecute implements IMessage {
   private static final int MAX_SCRIPTS = 512;
   public String script = "";
   public String function = "main";
   public String code = "";
   public Object[] args = new Object[0];
   public final Map<String, class_2487> scripts = new LinkedHashMap();

   public PacketClientScriptExecute() {
   }

   public PacketClientScriptExecute(String code, Object[] args) {
      this.script = "";
      this.function = "";
      this.code = clean(code);
      this.args = args == null ? new Object[0] : args;
   }

   public PacketClientScriptExecute(String script, String function, Map<String, class_2487> scripts) {
      this(script, function, scripts, (Object[])null);
   }

   public PacketClientScriptExecute(String script, String function, Map<String, class_2487> scripts, Object[] args) {
      this.script = clean(script);
      this.function = clean(function);
      if (this.function.isEmpty()) {
         this.function = "main";
      }
      this.args = args == null ? new Object[0] : args;
      if (scripts != null) {
         int count = 0;
         for (Map.Entry<String, class_2487> entry : scripts.entrySet()) {
            if (count++ >= MAX_SCRIPTS || entry.getValue() == null) {
               break;
            }
            this.scripts.put(clean(entry.getKey()), entry.getValue());
         }
      }
   }

   public void fromBytes(ByteBuf buf) {
      this.script = clean(ForgeByteBufUtils.readUTF8String(buf));
      this.function = clean(ForgeByteBufUtils.readUTF8String(buf));
      if (this.function.isEmpty()) {
         this.function = "main";
      }
      this.code = clean(ForgeByteBufUtils.readUTF8String(buf));
      this.args = readArgs(buf);
      this.scripts.clear();
      int count = Math.max(0, Math.min(MAX_SCRIPTS, buf.readInt()));
      for (int i = 0; i < count; ++i) {
         String id = clean(ForgeByteBufUtils.readUTF8String(buf));
         class_2487 tag = NBTUtils.readInfiniteTag(buf);
         if (!id.isEmpty() && tag != null) {
            this.scripts.put(id, tag);
         }
      }
   }

   public void toBytes(ByteBuf buf) {
      ForgeByteBufUtils.writeUTF8String(buf, clean(this.script));
      ForgeByteBufUtils.writeUTF8String(buf, clean(this.function));
      ForgeByteBufUtils.writeUTF8String(buf, clean(this.code));
      writeArgs(buf, this.args);
      int count = Math.min(MAX_SCRIPTS, this.scripts.size());
      buf.writeInt(count);
      int written = 0;
      for (Map.Entry<String, class_2487> entry : this.scripts.entrySet()) {
         if (written++ >= count) {
            break;
         }
         ForgeByteBufUtils.writeUTF8String(buf, clean(entry.getKey()));
         ForgeByteBufUtils.writeTag(buf, entry.getValue());
      }
   }

   private static void writeArgs(ByteBuf buf, Object[] args) {
      int count = args == null ? 0 : args.length;
      buf.writeInt(count);
      if (count == 0) {
         return;
      }

      for (Object arg : args) {
         if (arg instanceof Boolean) {
            buf.writeByte(1);
            buf.writeBoolean((Boolean)arg);
         } else if (arg instanceof Number) {
            buf.writeByte(2);
            buf.writeDouble(((Number)arg).doubleValue());
         } else if (arg instanceof String) {
            buf.writeByte(3);
            ForgeByteBufUtils.writeUTF8String(buf, (String)arg);
         } else {
            buf.writeByte(4);
            ForgeByteBufUtils.writeUTF8String(buf, arg == null ? "" : arg.toString());
         }
      }
   }

   private static Object[] readArgs(ByteBuf buf) {
      int count = Math.max(0, buf.readInt());
      Object[] args = new Object[count];

      for (int i = 0; i < count; ++i) {
         byte type = buf.readByte();
         if (type == 1) {
            args[i] = buf.readBoolean();
         } else if (type == 2) {
            args[i] = buf.readDouble();
         } else {
            args[i] = ForgeByteBufUtils.readUTF8String(buf);
         }
      }

      return args;
   }

   private static String clean(String value) {
      return value == null ? "" : value;
   }
}
