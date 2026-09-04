package mchorse.mappet.client.gui.utils.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import mchorse.mappet.compat.client.LegacyGlStateManager;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.mclib.utils.resources.ResourceLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;

public class ImageGraphic extends Graphic {
   public ResourceLocation picture;
   public int width;
   public int height;
   private transient List<class_1043> gifTextures;
   private transient List<Long> gifDurations;
   private transient List<class_2960> gifIds;
   private transient long gifStartedAt;
   private transient int gifFrame;
   private transient String gifKey;

   public ImageGraphic() {
   }

   public ImageGraphic(ResourceLocation picture, int x, int y, int w, int h, int width, int height, int primary) {
      this.picture = picture;
      this.pixels.set(x, y, w, h);
      this.primary = primary;
      this.width = width;
      this.height = height;
   }

   @Environment(EnvType.CLIENT)
   public void drawGraphic(Area area) {
      if (this.picture == null) {
         return;
      }

      class_2960 texture = this.picture.toIdentifier();
      if (texture.toString().toLowerCase(java.util.Locale.ROOT).endsWith(".gif")) {
         this.ensureGif(texture);
         if (this.gifTextures != null && !this.gifTextures.isEmpty()) {
            this.updateGifFrame();
            texture = this.gifIds.get(this.gifFrame);
         }
      }

      GuiDraw.bindColor(this.primary);
      int left = area.x;
      int top = area.y;
      class_310.method_1551().method_1531().method_22813(texture);
      LegacyGlStateManager.enableTexture2D();
      LegacyGlStateManager.enableAlpha();
      LegacyGlStateManager.enableBlend();
      GuiDraw.drawBillboard(left, top, 0, 0, area.w, area.h, this.width, this.height);
   }

   @Environment(EnvType.CLIENT)
   private void ensureGif(class_2960 texture) {
      String key = texture.toString();
      if (key.equals(this.gifKey) && this.gifTextures != null) {
         return;
      }
      this.closeGifTextures();
      this.gifKey = key;
      this.gifTextures = new ArrayList<>();
      this.gifDurations = new ArrayList<>();
      this.gifIds = new ArrayList<>();
      try {
         class_3298 resource = (class_3298)class_310.method_1551().method_1478().method_14486(texture).orElse(null);
         if (resource == null) {
            return;
         }
         try (InputStream input = resource.method_14482(); ImageInputStream stream = ImageIO.createImageInputStream(input)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) {
               return;
            }
            ImageReader reader = readers.next();
            try {
               reader.setInput(stream, false, false);
               int count = Math.min(64, reader.getNumImages(true));
               for (int index = 0; index < count; ++index) {
                  BufferedImage frame = reader.read(index);
                  if (frame == null || frame.getWidth() > 2048 || frame.getHeight() > 2048) {
                     continue;
                  }
                  class_1011 image = new class_1011(frame.getWidth(), frame.getHeight(), true);
                  for (int y = 0; y < frame.getHeight(); ++y) {
                     for (int x = 0; x < frame.getWidth(); ++x) {
                        image.method_4305(x, y, frame.getRGB(x, y));
                     }
                  }
                  class_1043 dynamic = new class_1043(image);
                  this.gifTextures.add(dynamic);
                  this.gifIds.add(class_310.method_1551().method_1531().method_4617("mappet_gif_" + Integer.toHexString(this.picture.hashCode()) + "_" + index, dynamic));
                  this.gifDurations.add(100L);
               }
            } finally {
               reader.dispose();
            }
         }
         this.gifStartedAt = System.currentTimeMillis();
         this.gifFrame = 0;
      } catch (Throwable ignored) {
         this.closeGifTextures();
      }
   }

   @Environment(EnvType.CLIENT)
   private void updateGifFrame() {
      if (this.gifTextures == null || this.gifTextures.size() < 2) {
         return;
      }
      long elapsed = Math.max(0L, System.currentTimeMillis() - this.gifStartedAt);
      long total = 0L;
      for (long duration : this.gifDurations) {
         total += Math.max(20L, duration);
      }
      long time = total == 0L ? 0L : elapsed % total;
      int next = 0;
      while (next < this.gifDurations.size() - 1 && time >= Math.max(20L, this.gifDurations.get(next))) {
         time -= Math.max(20L, this.gifDurations.get(next));
         ++next;
      }
      this.gifFrame = next;
   }

   @Environment(EnvType.CLIENT)
   private void closeGifTextures() {
      if (this.gifTextures != null) {
         for (class_1043 texture : this.gifTextures) {
            try {
               texture.close();
            } catch (Throwable ignored) {
            }
         }
      }
      this.gifTextures = null;
      this.gifDurations = null;
      this.gifIds = null;
      this.gifFrame = 0;
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      if (this.picture != null) {
         tag.method_10566("Image", RLUtils.writeNbt(this.picture));
      }
      tag.method_10569("Width", this.width);
      tag.method_10569("Height", this.height);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Image")) {
         this.picture = RLUtils.create(tag.method_10580("Image"));
      }
      this.width = tag.method_10550("Width");
      this.height = tag.method_10550("Height");
      this.gifKey = null;
   }
}
