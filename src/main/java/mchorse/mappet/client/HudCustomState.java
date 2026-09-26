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

   public static boolean hasTransform(String id) {
      return visible.containsKey(id) || positions.containsKey(id) || scales.containsKey(id);
   }

   public static void setVisible(String id, boolean value) {
      visible.put(id, value);
   }

   public static boolean isVisible(String id) {
      return visible.getOrDefault(id, true);
   }

   public static void setPosition(String id, int x, int y) {
      positions.put(id, new int[]{x, y});
   }

   public static int getX(String id) {
      int[] position = positions.get(id);
      return position == null ? 0 : position[0];
   }

   public static int getY(String id) {
      int[] position = positions.get(id);
      return position == null ? 0 : position[1];
   }

   public static void setScale(String id, float scale) {
      scales.put(id, scale);
   }

   public static float getScale(String id) {
      return scales.getOrDefault(id, 1.0F);
   }

   public static void reset(String id) {
      visible.remove(id);
      positions.remove(id);
      scales.remove(id);
   }

   public static void reset() {
      visible.clear();
      positions.clear();
      scales.clear();
   }
}