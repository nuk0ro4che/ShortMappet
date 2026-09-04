package mchorse.mappet.network.common.factions;

import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.factions.Faction;
import mchorse.mclib.network.ForgeByteBufUtils;
import mchorse.mclib.network.IMessage;
import mchorse.mclib.utils.NBTUtils;
import net.minecraft.class_2487;

public class PacketFactions implements IMessage {
   public Map<String, Faction> factions = new HashMap();
   public Map<String, Double> states = new HashMap();

   public PacketFactions() {
   }

   public PacketFactions(Map<String, Faction> factions, Map<String, Double> states) {
      this.factions.putAll(factions);
      this.states.putAll(states);
   }

   public void fromBytes(ByteBuf buf) {
      int i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         String key = ForgeByteBufUtils.readUTF8String(buf);
         class_2487 tag = NBTUtils.readInfiniteTag(buf);
         Faction faction = (Faction)Mappet.factions.create(key, tag);
         if (faction != null) {
            this.factions.put(key, faction);
         }
      }

      i = 0;

      for(int c = buf.readInt(); i < c; ++i) {
         String key = ForgeByteBufUtils.readUTF8String(buf);
         double value = buf.readDouble();
         this.states.put(key, value);
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.factions.size());

      for(Map.Entry<String, Faction> entry : this.factions.entrySet()) {
         ForgeByteBufUtils.writeUTF8String(buf, (String)entry.getKey());
         ForgeByteBufUtils.writeTag(buf, ((Faction)entry.getValue()).serializeNBT());
      }

      buf.writeInt(this.states.size());

      for(Map.Entry<String, Double> entry : this.states.entrySet()) {
         ForgeByteBufUtils.writeUTF8String(buf, (String)entry.getKey());
         buf.writeDouble((Double)entry.getValue());
      }

   }
}
