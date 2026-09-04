package mchorse.mappet.client;

import java.util.EnumMap;
import java.util.Map;

public final class HudVisibilityState {
   public enum Element {
      HOTBAR,
      HEALTH,
      HUNGER,
      EXPERIENCE,
      CROSSHAIR,
      STATUS_EFFECTS,
      MOUNT_HEALTH,
      VIGNETTE,
      SPYGLASS,
      CHAT,
      PLAYER_LIST,
      SCOREBOARD,
      BOSS_BAR,
      ITEM_TOOLTIP
   }

   private static final Map<Element, Boolean> visible = new EnumMap<>(Element.class);
   private static final Map<Element, int[]> positions = new EnumMap<>(Element.class);

   static {
      reset();
   }

   private HudVisibilityState() {
   }

   public static void set(Element element, boolean value) {
      visible.put(element, value);
   }

   public static boolean isVisible(Element element) {
      return visible.getOrDefault(element, true);
   }

   public static void setPosition(Element element, int x, int y) {
      positions.put(element, new int[]{x, y});
   }

   public static int getX(Element element) {
      int[] position = positions.get(element);
      return position == null ? 0 : position[0];
   }

   public static int getY(Element element) {
      int[] position = positions.get(element);
      return position == null ? 0 : position[1];
   }

   public static void resetPosition(Element element) {
      positions.remove(element);
   }

   public static void reset() {
      for (Element element : Element.values()) {
         visible.put(element, true);
         positions.remove(element);
      }
   }
}
