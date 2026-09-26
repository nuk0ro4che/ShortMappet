package mchorse.mappet.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Runtime transforms for dynamically detected (vanilla unknown / modded) HUD
 * elements. Every captured element is identified by its texture path, so
 * scripts can hide, move and scale them exactly like the vanilla elements.
 */
public final class HudCustomState {
   private static final Map<String, Boolean> visible = new HashMap();
   private static final Map<String, int[]> positions = new HashMap();
   private static final Map<String, Float> scales = new HashMap();

   private HudCustomState() {
   }

   private static String base(String id) {
      int idx = id.lastIndexOf('#');
      return idx > 0 ? id.substring(0, idx) : null;
   }

   private static <V> V resolve(Map<String, V> map, String id) {
      V value = map.get(id);

      if (value != null) {
         return value;
      }

      String baseId = base(id);
      return baseId == null ? null : map.get(baseId);
   }

   public static boolean hasTransform(String id) {
      if (visible.containsKey(id) || positions.containsKey(id) || scales.containsKey(id)) {
         return true;
      }

      String baseId = base(id);
      return baseId != null && (visible.containsKey(baseId) || positions.containsKey(baseId) || scales.containsKey(baseId));
   }

   public static void setVisible(String id, boolean value) {
      visible.put(id, value);
   }

   public static boolean isVisible(String id) {
      Boolean value = resolve(visible, id);
      return value == null || value;
   }

   public static void setPosition(String id, int x, int y) {
      positions.put(id, new int[]{x, y});
   }

   public static int getX(String id) {
      int[] position = resolve(positions, id);
      return position == null ? 0 : position[0];
   }

   public static int getY(String id) {
      int[] position = resolve(positions, id);
      return position == null ? 0 : position[1];
   }

   public static void setScale(String id, float scale) {
      scales.put(id, scale);
   }

   public static float getScale(String id) {
      Float scale = resolve(scales, id);
      return scale == null ? 1.0F : scale;
   }

   public static void reset(String id) {
      visible.remove(id);
      positions.remove(id);
      scales.remove(id);

      if (base(id) == null) {
         String prefix = id + "#";
         visible.keySet().removeIf(key -> key.startsWith(prefix));
         positions.keySet().removeIf(key -> key.startsWith(prefix));
         scales.keySet().removeIf(key -> key.startsWith(prefix));
      }
   }

   public static void reset() {
      visible.clear();
      positions.clear();
      scales.clear();
   }
}