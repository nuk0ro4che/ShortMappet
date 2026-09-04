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
   public final Map<String, class_2487> scripts = new LinkedHashMap();

   public PacketClientScriptExecute() {
   }

   public PacketClientScriptExecute(String script, String function, Map<String, class_2487> scripts) {
      this.script = clean(script);
      this.function = clean(function);
      if (this.function.isEmpty()) {
         this.function = "main";
      }
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

   private static String clean(String value) {
      return value == null ? "" : value;
   }
}
