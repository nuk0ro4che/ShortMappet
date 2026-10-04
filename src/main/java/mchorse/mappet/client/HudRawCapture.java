package mchorse.mappet.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.mixins.BufferBuilderAccessor;
import mchorse.mappet.mixins.TextureManagerAccessor;
import net.minecraft.class_1044;
import net.minecraft.class_2960;
import net.minecraft.class_310;

/**
 * Captures HUD draws that never touch {@link net.minecraft.class_332} (the
 * {@code DrawContext}). Mods like Forge's {@code GuiGraphics.blit} build their
 * quads straight on the Tesselator, so Mappet watches the raw vertices instead:
 * the position of every vertex is measured, matched with the element it
 * belonged to on the previous frame and moved/scaled/hidden right in the vertex
 * buffer.
 */
public final class HudRawCapture {
   private static final Map<Integer, class_2960> textures = new HashMap<>();

   private static boolean debug;
   private static boolean debugChecked;
   private static int frames;
   private static int logs;
   private static int batches;
   private static int writes;
   private static int inWindow;
   private static int accepted;
   private static int rejected;
   private static final java.util.Set<String> logged = new java.util.HashSet<>();
   private static boolean batch;
   private static boolean skip;
   private static boolean has;
   private static boolean resolved;
   private static boolean transform;
   private static boolean hidden;
   private static int mask;
   private static int vertex = Integer.MIN_VALUE;
   private static int minX;
   private static int minY;
   private static int maxX;
   private static int maxY;
   private static float anchorX;
   private static float anchorY;
   private static float dx;
   private static float dy;
   private static float scale;
   private static String path;
   private static String id;

   private HudRawCapture() {
   }

   /**
    * Every float written into the vertex buffer. The position of a vertex sits
    * in the element at offset 0 and is written as the floats 0, 4 and 8, while
    * {@code offset} points at the element of the vertex that is being written,
    * so the position is only met when the base is a whole vertex. Colors, UVs
    * and light of other formats are ignored this way.
    */
   public static void floatAt(BufferBuilderAccessor self, int index, float value) {
      boolean capturing = HudCapture.isCapturing();

      if (!capturing) {
         if (mappet$debug()) {
            writes++;
         }

         return;
      }

      inWindow++;
      int base = self.mappet$offset();
      net.minecraft.class_293 format = self.mappet$format();

      if (mappet$debug() && logs < 60) {
         logs++;
         Mappet.LOGGER.info("[hud-raw] float stride={} base={} index={} value={}", format == null ? -1 : format.method_1362(), base, index, value);
      }

      int stride = format == null ? 0 : format.method_1362();

      if (stride <= 0 || base % stride != 0 || index > 8) {
         rejected++;

         return;
      }

      accepted++;

      if (!batch) {
         if (skip) {
            return;
         }

         mappet$begin();

         if (!batch) {
            return;
         }
      }

      if (base != vertex) {
         vertex = base;
         mask = 0;
      }

      mask |= 1 << (index >> 2);

      if (mask != 7) {
         return;
      }

      mask = 0;

      float x = self.mappet$buffer().getFloat(base);
      float y = self.mappet$buffer().getFloat(base + 4);

      if (!resolved) {
         resolved = true;
         mappet$resolve(x, y);
      }

      if (!transform) {
         mappet$accumulate(x, y);

         return;
      }

      float tx = x;
      float ty = y;

      if (hidden) {
         /* Collapsing every vertex into a single point leaves a quad without any area, so nothing is drawn */
         tx = anchorX;
         ty = anchorY;
      } else {
         tx = anchorX + dx + (x - anchorX) * scale;
         ty = anchorY + dy + (y - anchorY) * scale;
      }

      mappet$accumulate(tx, ty);
      self.mappet$buffer().putFloat(base, tx);
      self.mappet$buffer().putFloat(base + 4, ty);
   }

   public static void endBatch() {
      if (mappet$debug()) {
         batches++;
      }

      if (batch && has) {
         if (mappet$bounds(minX, minY, maxX, maxY)) {
            String key = HudCapture.rawDrawAndKey(path, minX, minY, maxX, maxY);

            if (mappet$debug() && logged.add(key + "@" + minX + "," + minY)) {
               Mappet.LOGGER.info("[hud-raw] batch {} {}x{} at {} {} - {} {} -> {} moved={} dx={} dy={} scale={} hidden={}", path, maxX - minX, maxY - minY, minX, minY, maxX, maxY, key, transform, dx, dy, scale, hidden);
            }
         } else if (mappet$debug()) {
            Mappet.LOGGER.info("[hud-raw] skipped out of bounds {} {}x{} at {} {} - {} {}", path, maxX - minX, maxY - minY, minX, minY, maxX, maxY);
         }
      }

      if (transform && !hidden) {
         HudCapture.storeApplied(id, HudCustomState.getX(id), HudCustomState.getY(id));
      }

      logged.clear();

      batch = false;
      skip = false;
      resolved = false;
      has = false;
      transform = false;
      hidden = false;
      mask = 0;
      vertex = Integer.MIN_VALUE;
      id = null;
   }

   private static void mappet$begin() {
      batch = false;
      resolved = false;
      has = false;
      transform = false;
      hidden = false;
      mask = 0;
      vertex = Integer.MIN_VALUE;
      id = null;

      class_2960 texture = mappet$boundTexture();

      if (texture == null) {
         if (mappet$debug()) {
            Mappet.LOGGER.info("[hud-raw] batch without a known texture (gl={})", RenderSystem.getShaderTexture(0));
         }

         skip = true;

         return;
      }

      if (!HudCapture.isRawCandidate(texture.toString())) {
         skip = true;

         return;
      }

      batch = true;
      path = texture.toString();
   }

   private static void mappet$resolve(float x, float y) {
      id = HudCapture.peekId(path, (int)x, (int)y);

      if (id == null || HudCapture.isHandled(id) || !HudCustomState.hasTransform(id)) {
         id = null;

         return;
      }

      int[] box = HudCapture.box(id);

      if (box == null) {
         id = null;

         return;
      }

      transform = true;
      hidden = !HudCustomState.isVisible(id);
      anchorX = box[0];
      anchorY = box[1];
      dx = HudCustomState.getX(id) - HudCapture.appliedX(id);
      dy = HudCustomState.getY(id) - HudCapture.appliedY(id);

      float value = HudCustomState.getScale(id);
      scale = value > 0.05F ? value : 1.0F;
   }

   private static void mappet$accumulate(float x, float y) {
      int px = (int)Math.floor(x);
      int py = (int)Math.floor(y);

      if (!has) {
         has = true;
         minX = px;
         minY = py;
         maxX = px;
         maxY = py;

         return;
      }

      if (px < minX) {
         minX = px;
      }

      if (py < minY) {
         minY = py;
      }

      if (px > maxX) {
         maxX = px;
      }

      if (py > maxY) {
         maxY = py;
      }
   }

   /** Raw geometry of non-GUI passes can land far outside of the screen, so it is never a HUD element */
   private static boolean mappet$bounds(int x0, int y0, int x1, int y1) {
      int[] frame = HudCapture.frameBuffer();

      if (frame == null) {
         return true;
      }

      int w = frame[0];
      int h = frame[1];

      return x0 >= -w / 4 && y0 >= -h / 4 && x1 <= w + w / 4 && y1 <= h + h / 4 && (x1 - x0) * (y1 - y0) < w * h;
   }

   /** Called on every captured frame to keep the debug output bounded and readable */
   public static void frame() {
      if (!mappet$debug()) {
         return;
      }

      frames++;
      logs = 0;

      if (frames % 60 == 0) {
         Mappet.LOGGER.info("[hud-raw] frame {}: batches={} writes={} inWindow={} accepted={} rejected={}", frames, batches, writes, inWindow, accepted, rejected);
         HudIdPicker.debugState();
      }

      batches = 0;
      writes = 0;
      inWindow = 0;
      accepted = 0;
      rejected = 0;
   }

   private static boolean mappet$debug() {
      if (!debugChecked) {
         debugChecked = true;
         debug = new File("mappet_hud_debug").exists();

         if (debug) {
            Mappet.LOGGER.info("[hud-raw] debug logging is on");
         }
      }

      return debug;
   }

   /**
    * {@link RenderSystem} only keeps the GL id of the bound shader texture, so
    * the name is resolved back through the texture manager (and cached, since a
    * batch keeps the same texture for all of its vertices).
    */
   private static class_2960 mappet$boundTexture() {
      int gl = RenderSystem.getShaderTexture(0);

      if (gl <= 0) {
         return null;
      }

      class_2960 cached = textures.get(gl);

      if (cached != null) {
         return cached;
      }

      class_310 client = class_310.method_1551();

      if (client == null || client.method_1531() == null) {
         return null;
      }

      Map<class_2960, class_1044> registered = ((TextureManagerAccessor) client.method_1531()).mappet$textures();

      if (registered == null || registered.isEmpty()) {
         return null;
      }

      for (Map.Entry<class_2960, class_1044> entry : registered.entrySet()) {
         class_1044 texture = entry.getValue();

         if (texture != null && texture.method_4624() == gl) {
            textures.put(gl, entry.getKey());

            return entry.getKey();
         }
      }

      return null;
   }
}