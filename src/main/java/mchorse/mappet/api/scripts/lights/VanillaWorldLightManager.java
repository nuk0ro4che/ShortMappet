package mchorse.mappet.api.scripts.lights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketVirtualWorldLight;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2350;
import net.minecraft.class_3218;






public final class VanillaWorldLightManager {
   private static final int MAX_RAY_SAMPLES = 96;
   private static final Map<class_1937, Map<String, LightEntry>> LIGHTS = new HashMap<>();
   private static final Map<Object, Map<class_2338, Map<String, Integer>>> SOURCES = new HashMap<>();

   private VanillaWorldLightManager() {
   }

   public static void displayPoint(class_3218 world, String id, int duration, double x, double y, double z, int level) {
      if (!isValid(world, id, x, y, z)) {
         return;
      }

      LightEntry entry = new LightEntry(id, clampDuration(duration));
      class_2338 position = toBlockPos(x, y, z);
      if (canHostSource(world, position, false)) {
         entry.put(position, clampLevel(level));
      }
      replaceServer(world, entry);
   }

   
   public static void displayPointClient(class_1937 world, String id, int duration, double x, double y, double z, int level) {
      if (!isValid(world, id, x, y, z)) {
         return;
      }
      LightEntry entry = new LightEntry(id, clampDuration(duration));
      class_2338 position = toBlockPos(x, y, z);
      if (canHostSource(world, position, false)) {
         entry.put(position, clampLevel(level));
      }
      replaceInternal(world, entry);
   }

   public static void displayRay(class_3218 world, String id, int duration, double x, double y, double z, double endX, double endY, double endZ, int level, float spacing) {
      if (!isValid(world, id, x, y, z) || !isFinite(endX, endY, endZ)) {
         return;
      }

      LightEntry entry = new LightEntry(id, clampDuration(duration));
      double deltaX = endX - x;
      double deltaY = endY - y;
      double deltaZ = endZ - z;
      double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
      int samples = Math.max(1, Math.min(MAX_RAY_SAMPLES, (int)Math.ceil(length / (double)clampSpacing(spacing)) + 1));

      for(int index = 0; index < samples; ++index) {
         double progress = samples == 1 ? 0.0D : (double)index / (double)(samples - 1);
         class_2338 position = toBlockPos(x + deltaX * progress, y + deltaY * progress, z + deltaZ * progress);
         if (canHostSource(world, position, false)) {
            entry.put(position, clampLevel(level));
         }
      }

      replaceServer(world, entry);
   }

   
   public static void displayRayClient(class_1937 world, String id, int duration, double x, double y, double z, double endX, double endY, double endZ, int level, float spacing) {
      if (!isValid(world, id, x, y, z) || !isFinite(endX, endY, endZ)) {
         return;
      }
      LightEntry entry = new LightEntry(id, clampDuration(duration));
      double deltaX = endX - x;
      double deltaY = endY - y;
      double deltaZ = endZ - z;
      double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
      int samples = Math.max(1, Math.min(MAX_RAY_SAMPLES, (int)Math.ceil(length / (double)clampSpacing(spacing)) + 1));
      for(int index = 0; index < samples; ++index) {
         double progress = samples == 1 ? 0.0D : (double)index / (double)(samples - 1);
         class_2338 position = toBlockPos(x + deltaX * progress, y + deltaY * progress, z + deltaZ * progress);
         if (canHostSource(world, position, false)) {
            entry.put(position, clampLevel(level));
         }
      }
      replaceInternal(world, entry);
   }

   
   public static void displayFlashlight(class_3218 world, String id, int duration, double x, double y, double z, double endX, double endY, double endZ, int startLevel, int endLevel, float spacing) {
      displayFlashlight(world, id, duration, x, y, z, endX, endY, endZ, startLevel, endLevel, spacing, false);
   }

   



   public static void displayFlashlight(class_3218 world, String id, int duration, double x, double y, double z, double endX, double endY, double endZ, int startLevel, int endLevel, float spacing, boolean ignoredNonSolidFlag) {
      LightEntry entry = createFlashlight(world, id, duration, x, y, z, endX, endY, endZ, startLevel, endLevel, spacing, ignoredNonSolidFlag);
      if (entry != null) {
         replaceServer(world, entry);
      }
   }

   
   public static void displayFlashlightClient(class_1937 world, String id, int duration, double x, double y, double z, double endX, double endY, double endZ, int startLevel, int endLevel, float spacing, boolean ignoredNonSolidFlag) {
      LightEntry entry = createFlashlight(world, id, duration, x, y, z, endX, endY, endZ, startLevel, endLevel, spacing, ignoredNonSolidFlag);
      if (entry != null) {
         replaceInternal(world, entry);
      }
   }

   private static LightEntry createFlashlight(class_1937 world, String id, int duration, double x, double y, double z, double endX, double endY, double endZ, int startLevel, int endLevel, float spacing, boolean ignoredNonSolidFlag) {
      if (!isValid(world, id, x, y, z) || !isFinite(endX, endY, endZ)) {
         return null;
      }

      LightEntry entry = new LightEntry(id, clampDuration(duration));
      double deltaX = endX - x;
      double deltaY = endY - y;
      double deltaZ = endZ - z;
      double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
      int samples = Math.max(1, Math.min(MAX_RAY_SAMPLES, (int)Math.ceil(length / (double)clampSpacing(spacing)) + 1));

      for(int index = 0; index < samples; ++index) {
         double progress = samples == 1 ? 1.0D : (double)index / (double)(samples - 1);
         int level = Math.round((float)startLevel + ((float)endLevel - (float)startLevel) * (float)progress);
         class_2338 position = toBlockPos(x + deltaX * progress, y + deltaY * progress, z + deltaZ * progress);
         if (canHostSource(world, position, ignoredNonSolidFlag)) {
            entry.put(position, clampLevel(level));
         }
      }

      return entry;
   }


   
   public static synchronized int getVirtualEmission(Object world, class_2338 position) {
      Map<class_2338, Map<String, Integer>> sources = SOURCES.get(world);
      if (sources == null) {
         return 0;
      }

      Map<String, Integer> levels = sources.get(position);
      if (levels == null) {
         return 0;
      }

      int highest = 0;
      for(int level : levels.values()) {
         highest = Math.max(highest, level);
      }

      return highest;
   }

   
   public static void applyClient(class_1937 world, String id, int duration, long[] positions, byte[] levels) {
      applyClient(world, id, duration, positions, levels, 0xffffffff);
   }

   public static void applyClient(class_1937 world, String id, int duration, long[] positions, byte[] levels, int color) {
      if (world == null || id == null || id.isEmpty()) {
         return;
      }

      LightEntry entry = new LightEntry(id, duration);
      int count = Math.min(positions == null ? 0 : positions.length, levels == null ? 0 : levels.length);
      for(int index = 0; index < count; ++index) {
         entry.put(class_2338.method_10092(positions[index]), clampLevel(levels[index]));
      }

      replaceInternal(world, entry);
   }

   
   public static void removeClient(class_1937 world, String id) {
      if (world != null && id != null) {
         removeInternal(world, id);
      }
   }

   public static void remove(class_3218 world, String id) {
      if (world != null && id != null && removeInternal(world, id)) {
         sendRemove(world, id);
      }
   }

   public static synchronized void tick() {
      for(class_1937 world : new ArrayList<>(LIGHTS.keySet())) {
         Map<String, LightEntry> entries = LIGHTS.get(world);
         if (entries == null) {
            continue;
         }

         List<String> expired = new ArrayList<>();
         for(LightEntry entry : entries.values()) {
            if (entry.duration >= 0 && ++entry.age > entry.duration) {
               expired.add(entry.id);
            }
         }

         for(String id : expired) {
            if (removeInternal(world, id) && world instanceof class_3218 serverWorld) {
               sendRemove(serverWorld, id);
            }
         }
      }
   }

   public static synchronized void clear() {
      for(class_1937 world : new ArrayList<>(LIGHTS.keySet())) {
         Map<String, LightEntry> entries = LIGHTS.get(world);
         if (entries != null) {
            for(String id : new ArrayList<>(entries.keySet())) {
               removeInternal(world, id);
            }
         }
      }

      LIGHTS.clear();
      SOURCES.clear();
   }

   private static void replaceServer(class_3218 world, LightEntry entry) {
      replaceInternal(world, entry);
      long[] positions = new long[entry.levels.size()];
      byte[] levels = new byte[entry.levels.size()];
      int index = 0;
      for(Map.Entry<class_2338, Integer> value : entry.levels.entrySet()) {
         positions[index] = value.getKey().method_10063();
         levels[index] = (byte)(int)value.getValue();
         ++index;
      }

      PacketVirtualWorldLight message = PacketVirtualWorldLight.set(entry.id, entry.duration, positions, levels);
      for(net.minecraft.class_3222 player : world.method_18456()) {
         Dispatcher.sendTo(message, player);
      }
   }

   private static void sendRemove(class_3218 world, String id) {
      PacketVirtualWorldLight message = PacketVirtualWorldLight.remove(id);
      for(net.minecraft.class_3222 player : world.method_18456()) {
         Dispatcher.sendTo(message, player);
      }
   }

   private static synchronized void replaceInternal(class_1937 world, LightEntry entry) {
      removeInternal(world, entry.id);
      if (entry.levels.isEmpty()) {
         return;
      }

      LIGHTS.computeIfAbsent(world, (key) -> new HashMap<>()).put(entry.id, entry);
      Map<class_2338, Map<String, Integer>> sources = SOURCES.computeIfAbsent(world, (key) -> new HashMap<>());
      for(Map.Entry<class_2338, Integer> value : entry.levels.entrySet()) {
         sources.computeIfAbsent(value.getKey(), (key) -> new HashMap<>()).put(entry.id, value.getValue());
         refreshLight(world, value.getKey());
      }
   }

   private static synchronized boolean removeInternal(class_1937 world, String id) {
      Map<String, LightEntry> entries = LIGHTS.get(world);
      if (entries == null) {
         return false;
      }

      LightEntry entry = entries.remove(id);
      if (entry == null) {
         return false;
      }

      Map<class_2338, Map<String, Integer>> sources = SOURCES.get(world);
      if (sources != null) {
         for(class_2338 position : entry.levels.keySet()) {
            Map<String, Integer> levels = sources.get(position);
            if (levels != null) {
               levels.remove(id);
               if (levels.isEmpty()) {
                  sources.remove(position);
               }
               refreshLight(world, position);
            }
         }

         if (sources.isEmpty()) {
            SOURCES.remove(world);
         }
      }

      if (entries.isEmpty()) {
         LIGHTS.remove(world);
      }

      return true;
   }

   private static void refreshLight(class_1937 world, class_2338 position) {
      world.method_22336().method_15513(position);
      for(class_2350 direction : class_2350.values()) {
         world.method_22336().method_15513(position.method_10093(direction));
      }
   }

   private static boolean canHostSource(class_1937 world, class_2338 position, boolean nonSolidIgnore) {
      class_2680 state = world.method_8320(position);
      return state.method_26215() || nonSolidIgnore && !state.method_26212(world, position) && !state.method_51176();
   }

   private static class_2338 toBlockPos(double x, double y, double z) {
      return new class_2338((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
   }

   private static boolean isValid(class_1937 world, String id, double x, double y, double z) {
      return world != null && id != null && !id.isEmpty() && isFinite(x, y, z);
   }

   private static int clampDuration(int duration) {
      return Math.max(-1, Math.min(72000, duration));
   }

   private static int clampLevel(int level) {
      return Math.max(1, Math.min(15, level));
   }

   private static float clampSpacing(float spacing) {
      return Math.max(0.25F, Math.min(4.0F, spacing));
   }

   private static boolean isFinite(double x, double y, double z) {
      return Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z);
   }

   private static class LightEntry {
      public final String id;
      public final int duration;
      public final Map<class_2338, Integer> levels = new HashMap<>();
      public int age;

      public LightEntry(String id, int duration) {
         this.id = id;
         this.duration = duration;
      }

      public void put(class_2338 position, int level) {
         this.levels.merge(position, level, Math::max);
      }
   }
}
