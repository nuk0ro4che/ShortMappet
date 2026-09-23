package mchorse.mappet.network.common.scripts;

import io.netty.buffer.ByteBuf;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import mchorse.mappet.api.vision.VisionZone;
import mchorse.mappet.api.vision.VisionZoneManager;
import mchorse.mclib.network.IMessage;

public class PacketVisionZone implements IMessage {
   public Map<UUID, VisionZone> zones = new LinkedHashMap<>();

   public PacketVisionZone() {
      this.zones.putAll(VisionZoneManager.serverZones);
   }

   public void fromBytes(ByteBuf buf) {
      int size = buf.readInt();

      for (int i = 0; i < size; i++) {
         long msb = buf.readLong();
         long lsb = buf.readLong();
         this.zones.put(new UUID(msb, lsb), VisionZone.read(buf));
      }
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.zones.size());

      for (Map.Entry<UUID, VisionZone> entry : this.zones.entrySet()) {
         buf.writeLong(entry.getKey().getMostSignificantBits());
         buf.writeLong(entry.getKey().getLeastSignificantBits());
         entry.getValue().write(buf);
      }
   }
}