package mchorse.mappet.api.vision;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketVisionZone;

public class VisionZoneManager {
   public static final Map<UUID, VisionZone> serverZones = new HashMap<>();
   public static final Map<UUID, VisionZone> clientZones = new HashMap<>();

   public static void setZone(UUID uuid, VisionZone zone) {
      if (zone == null) {
         VisionZoneManager.removeZone(uuid);
         return;
      }

      VisionZoneManager.serverZones.put(uuid, zone);
      VisionZoneManager.sync();
   }

   public static void setZoneClient(UUID uuid, VisionZone zone) {
      if (zone == null) {
         VisionZoneManager.clientZones.remove(uuid);
         return;
      }

      VisionZoneManager.clientZones.put(uuid, zone);
   }

   public static void removeZone(UUID uuid) {
      if (uuid == null) {
         return;
      }

      boolean server = VisionZoneManager.serverZones.remove(uuid) != null;
      VisionZoneManager.clientZones.remove(uuid);

      if (server) {
         VisionZoneManager.sync();
      }
   }

   public static VisionZone getServer(UUID uuid) {
      return uuid == null ? null : VisionZoneManager.serverZones.get(uuid);
   }

   public static VisionZone getClient(UUID uuid) {
      return uuid == null ? null : VisionZoneManager.clientZones.get(uuid);
   }

   public static void sync() {
      Dispatcher.DISPATCHER.sendToAll(new PacketVisionZone());
   }

   public static void clearServer() {
      if (!VisionZoneManager.serverZones.isEmpty()) {
         VisionZoneManager.serverZones.clear();
         VisionZoneManager.sync();
      }
   }

   public static void applyClient(Map<UUID, VisionZone> zones) {
      VisionZoneManager.clientZones.clear();
      VisionZoneManager.clientZones.putAll(zones);
   }
}