package mchorse.mappet.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import org.joml.Matrix4f;

/**
 * Measures the real drawn bounds of every HUD element (and unknown/modded
 * HUD draws) while {@link net.minecraft.class_329} is rendering.
 *
 * Unknown draws (modded HUDs) are grouped into spatial clusters, one per
 * meter/panel: all blits of the same texture that land within {@link #TOL}
 * pixels of each other belong to one cluster and get a stable id like
 * {@code texture#0}, {@code texture#1}, ... Stable across frames and even
 * across being hidden/re-shown.
 */
public final class HudCapture {
   public static final class CustomBox {
      public final String texture;
      public final int x;
      public final int y;
      public final int x2;
      public final int y2;

      public CustomBox(String texture, int x, int y, int x2, int y2) {
         this.texture = texture;
         this.x = x;
         this.y = y;
         this.x2 = x2;
         this.y2 = y2;
      }
   }

   public static final class CustomDraw {
      public final String texture;
      public final int x;
      public final int y;
      public final int x2;
      public final int y2;

      public CustomDraw(String texture, int x, int y, int x2, int y2) {
         this.texture = texture;
         this.x = x;
         this.y = y;
         this.x2 = x2;
         this.y2 = y2;
      }
   }

   private static final class Cluster {
      final String key;
      final String sizeKey;
      int[] box;
      int[] frameBox;
      int appliedX;
      int appliedY;

      Cluster(String key, String sizeKey) {
         this.key = key;
         this.sizeKey = sizeKey;
      }
   }

   private static final int TOL = 10;
   private static final int XTOL = TOL;
   private static final int YTOL = 2;
   private static final int CLUSTER_VERSION = 3;
   private static int clusterVersion = 0;
   private static final Map<HudVisibilityState.Element, int[]> boxes = new EnumMap(HudVisibilityState.Element.class);
   private static final Map<String, List<Cluster>> clustersByTexture = new HashMap();
   private static final Map<String, Integer> clusterCounters = new HashMap();
   private static final Map<String, int[]> lastSeen = new HashMap();
   private static final Set<String> touched = new HashSet();
   private static List<CustomBox> customBoxes = new ArrayList();
   private static List<CustomDraw> customDraws = new ArrayList();
   private static HudVisibilityState.Element tag;
   private static HudVisibilityState.Element savedTag;
   private static boolean capturing;
   private static final Set<String> VANILLA_HUD_TEXTURES = new HashSet<>(Arrays.asList(
      "minecraft:textures/gui/icons.png",
      "minecraft:textures/gui/hotbar.png",
      "minecraft:textures/gui/widgets.png",
      "minecraft:textures/gui/bars.png",
      "minecraft:textures/gui/vignette.png",
      "minecraft:textures/gui/spectator.png",
      "minecraft:textures/gui/options_background.png"
   ));

   private HudCapture() {
   }

   public static boolean isCapturing() {
      return capturing;
   }

   public static void begin() {
      if (clusterVersion != CLUSTER_VERSION) {
         clustersByTexture.clear();
         clusterCounters.clear();
         lastSeen.clear();
         clusterVersion = CLUSTER_VERSION;
      }

      boxes.clear();
      customDraws = new ArrayList();
      customBoxes = new ArrayList();
      touched.clear();
      tag = null;
      savedTag = null;
      capturing = true;
      for (Map.Entry<String, List<Cluster>> entry : clustersByTexture.entrySet()) {
         for (Cluster cluster : entry.getValue()) {
            cluster.frameBox = null;
         }
      }
   }

   public static void finish() {
      capturing = false;
      customBoxes = new ArrayList();

      for (Map.Entry<String, List<Cluster>> entry : clustersByTexture.entrySet()) {
         for (Cluster cluster : entry.getValue()) {
            if (cluster.frameBox != null) {
               cluster.box = cluster.frameBox;
               if (cluster.box[2] > cluster.box[0] && cluster.box[3] > cluster.box[1]) {
                  lastSeen.put(cluster.key, cluster.box);
                  customBoxes.add(new CustomBox(cluster.key, cluster.box[0], cluster.box[1], cluster.box[2], cluster.box[3]));
               }
            }
         }
      }
   }

   public static List<CustomBox> getCustomBoxes() {
      return customBoxes;
   }

   public static List<CustomDraw> getCustomDraws() {
      return customDraws;
   }

   public static Map<String, int[]> getLastSeen() {
      return lastSeen;
   }

   public static void beginElement(HudVisibilityState.Element element) {
      savedTag = tag;
      tag = element;
   }

   public static void endElement() {
      tag = savedTag;
   }

   private static void union(int[] into, int x0, int y0, int x1, int y1) {
      if (into[0] > x0) {
         into[0] = x0;
      }

      if (into[1] > y0) {
         into[1] = y0;
      }

      if (into[2] < x1) {
         into[2] = x1;
      }

      if (into[3] < y1) {
         into[3] = y1;
      }
   }

   /**
    * Routes a draw: vanilla tagged elements go into the element measurement,
    * everything else is clustered and the stable per-meter id is returned.
    */
   public static String drawAndKey(class_332 context, class_2960 texture, int x, int y, int width, int height, boolean forceTag) {
      if (!capturing) {
         return null;
      }

      Matrix4f matrix = context.method_51448().method_23760().method_23761();
      int x0 = x + (int)matrix.m30();
      int y0 = y + (int)matrix.m31();
      int x1 = x0 + width;
      int y1 = y0 + height;
      if (x1 <= x0 || y1 <= y0) {
         return null;
      }

      String path = texture.toString();
      boolean vanillaTexture = forceTag || VANILLA_HUD_TEXTURES.contains(path);
      if (tag != null && vanillaTexture) {
         int[] box = boxes.get(tag);
         if (box == null) {
            boxes.put(tag, new int[]{x0, y0, x1, y1});
         } else {
            union(box, x0, y0, x1, y1);
         }

         return null;
      }

      List<Cluster> clusters = clustersByTexture.computeIfAbsent(path, (k) -> new ArrayList());
      String sizeKey = (width / 4) + "x" + (height / 4);
      Cluster match = null;
      for (Cluster cluster : clusters) {
         if (!cluster.sizeKey.equals(sizeKey)) {
            continue;
         }

         int[] b = cluster.box;
         if (b == null) {
            continue;
         }

         if (x0 <= b[2] + XTOL && x1 >= b[0] - XTOL && y0 <= b[3] + YTOL && y1 >= b[1] - YTOL) {
            match = cluster;
            break;
         }
      }

      if (match == null) {
         int n = clusterCounters.merge(path, 1, Integer::sum) - 1;
         match = new Cluster(path + "#" + n, sizeKey);
         match.box = new int[]{x0, y0, x1, y1};
         clusters.add(match);
      }

      int[] f = match.frameBox;
      if (f == null) {
         match.frameBox = new int[]{x0, y0, x1, y1};
      } else {
         union(f, x0, y0, x1, y1);
      }

      touched.add(match.key);
      customDraws.add(new CustomDraw(match.key, x0, y0, x1, y1));
      return match.key;
   }

   public static void captureSprite(class_332 context, class_2960 texture, int x, int y, int width, int height) {
      drawAndKey(context, texture, x, y, width, height, true);
   }

   public static int appliedX(String key) {
      for (List<Cluster> clusters : clustersByTexture.values()) {
         for (Cluster cluster : clusters) {
            if (cluster.key.equals(key)) {
               return cluster.appliedX;
            }
         }
      }

      return 0;
   }

   public static int appliedY(String key) {
      for (List<Cluster> clusters : clustersByTexture.values()) {
         for (Cluster cluster : clusters) {
            if (cluster.key.equals(key)) {
               return cluster.appliedY;
            }
         }
      }

      return 0;
   }

   public static void storeApplied(String key, int x, int y) {
      for (List<Cluster> clusters : clustersByTexture.values()) {
         for (Cluster cluster : clusters) {
            if (cluster.key.equals(key)) {
               cluster.appliedX = x;
               cluster.appliedY = y;
               return;
            }
         }
      }
   }

   public static int[] box(HudVisibilityState.Element element) {
      return boxes.get(element);
   }
}