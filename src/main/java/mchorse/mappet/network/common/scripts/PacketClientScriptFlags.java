package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;

public class PacketClientScriptFlags implements IMessage {
   private static final int MAX_IDS = 8192;

   public final List<String> ids = new ArrayList<>();

   public PacketClientScriptFlags() {
   }

   public PacketClientScriptFlags(Collection<String> ids) {
      if (ids != null) {
         for (String id : ids) {
            if (this.ids.size() >= MAX_IDS) {
               break;
            }
            if (id != null && !id.isEmpty()) {
               this.ids.add(id);
            }
         }
      }
   }

   @Override
   public void fromBytes(ByteBuf buf) {
      this.ids.clear();
      int size = Math.min(Math.max(buf.readInt(), 0), MAX_IDS);

      for (int i = 0; i < size; i++) {
         String id = ForgeByteBufUtils.readUTF8String(buf);
         if (id != null && !id.isEmpty()) {
            this.ids.add(id);
         }
      }
   }

   @Override
   public void toBytes(ByteBuf buf) {
      int size = Math.min(this.ids.size(), MAX_IDS);
      buf.writeInt(size);

      for (int i = 0; i < size; i++) {
         ForgeByteBufUtils.writeUTF8String(buf, this.ids.get(i));
      }
   }
}