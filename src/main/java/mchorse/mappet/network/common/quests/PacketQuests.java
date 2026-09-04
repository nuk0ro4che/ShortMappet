package mchorse.mappet.network.common.quests;

import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.quests.Quests;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;

public class PacketQuests implements IMessage {
   public Map<String, Quest> quests = new LinkedHashMap();

   public PacketQuests() {
   }

   public PacketQuests(Quests quests) {
      this.quests.putAll(quests.quests);
   }

   public void fromBytes(ByteBuf buf) {
      int i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         String id = ForgeByteBufUtils.readUTF8String(buf);
         this.quests.put(id, (Quest)Mappet.quests.create(id, NBTUtils.readInfiniteTag(buf)));
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.quests.size());

      for(Map.Entry<String, Quest> entry : this.quests.entrySet()) {
         ForgeByteBufUtils.writeUTF8String(buf, (String)entry.getKey());
         ForgeByteBufUtils.writeTag(buf, ((Quest)entry.getValue()).serializeNBT());
      }

   }
}
