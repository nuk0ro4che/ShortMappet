package mchorse.mappet.client.renders.tile;

import mchorse.mappet.api.regions.shapes.AbstractShape;
import mchorse.mappet.api.regions.shapes.BoxShape;
import mchorse.mappet.api.regions.shapes.CylinderShape;
import mchorse.mappet.api.regions.shapes.SphereShape;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.tile.TileRegion;
import mchorse.mclib.utils.Color;
import net.minecraft.class_1921;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_761;
import org.joml.Matrix4f;

public class TileRegionRenderer extends TileBaseBlockRenderer<TileRegion> {
   private static final int SEGMENTS = 32;
   private static final Color SELECTED = new Color(0.0F, 0.5F, 1.0F, 0.5F);
   private static final Color NORMAL = new Color(1.0F, 1.0F, 1.0F, 1.0F);
   private TileRegion selected;

   public TileRegionRenderer() {
      super(new Color(1.0F, 0.098F, 0.72F, 0.5F));
   }

   protected Color getBoxColor(TileRegion tile) {
      return tile == this.selected ? SELECTED : super.getBoxColor(tile);
   }

   protected boolean canRender(class_310 client, TileRegion tile) {
      class_437 var4 = client.field_1755;
      if (var4 instanceof GuiMappetDashboard dashboard) {
         if (dashboard.panels.view.delegate == dashboard.region) {
            this.selected = dashboard.region.getTile();
            return true;
         }
      }

      this.selected = null;
      return super.canRender(client, tile);
   }

   protected void renderMoreDebug(TileRegion tile, float tickDelta, class_4587 matrices, class_4597 consumers, int light, int overlay) {
      Color color = tile == this.selected ? SELECTED : NORMAL;
      class_4588 lines = consumers.getBuffer(class_1921.method_23594());

      for(AbstractShape shape : tile.region.shapes) {
         this.renderShape(shape, matrices, lines, color);
      }

   }

   private void renderShape(AbstractShape shape, class_4587 matrices, class_4588 lines, Color color) {
      matrices.method_22903();
      matrices.method_22904(shape.pos.x + (double)0.5F, shape.pos.y + (double)0.5F, shape.pos.z + (double)0.5F);
      if (shape instanceof BoxShape box) {
         class_761.method_22980(matrices, lines, -box.size.x, -box.size.y, -box.size.z, box.size.x, box.size.y, box.size.z, color.r, color.g, color.b, color.a);
      } else if (shape instanceof CylinderShape cylinder) {
         for(int i = 0; i < 32; ++i) {
            double a = (double)i * Math.PI * (double)2.0F / (double)32.0F;
            double b = (double)(i + 1) * Math.PI * (double)2.0F / (double)32.0F;
            double ax = Math.cos(a) * cylinder.horizontal;
            double az = Math.sin(a) * cylinder.horizontal;
            double bx = Math.cos(b) * cylinder.horizontal;
            double bz = Math.sin(b) * cylinder.horizontal;
            this.line(matrices, lines, ax, cylinder.vertical, az, bx, cylinder.vertical, bz, color);
            this.line(matrices, lines, ax, -cylinder.vertical, az, bx, -cylinder.vertical, bz, color);
            this.line(matrices, lines, ax, -cylinder.vertical, az, ax, cylinder.vertical, az, color);
         }
      } else if (shape instanceof SphereShape sphere) {
         for(int ring = 0; ring < 4; ++ring) {
            for(int i = 0; i < 32; ++i) {
               double a = (double)i * Math.PI * (double)2.0F / (double)32.0F;
               double b = (double)(i + 1) * Math.PI * (double)2.0F / (double)32.0F;
               double yaw = (double)ring * Math.PI / (double)4.0F;
               double ax = Math.cos(a) * sphere.horizontal;
               double ay = Math.sin(a) * sphere.vertical;
               double bx = Math.cos(b) * sphere.horizontal;
               double by = Math.sin(b) * sphere.vertical;
               this.line(matrices, lines, ax * Math.cos(yaw), ay, ax * Math.sin(yaw), bx * Math.cos(yaw), by, bx * Math.sin(yaw), color);
            }
         }
      }

      matrices.method_22909();
   }

   private void line(class_4587 matrices, class_4588 out, double x1, double y1, double z1, double x2, double y2, double z2, Color c) {
      Matrix4f matrix = matrices.method_23760().method_23761();
      float dx = (float)(x2 - x1);
      float dy = (float)(y2 - y1);
      float dz = (float)(z2 - z1);
      float length = (float)Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
      if (length != 0.0F) {
         dx /= length;
         dy /= length;
         dz /= length;
         out.method_22918(matrix, (float)x1, (float)y1, (float)z1).method_22915(c.r, c.g, c.b, c.a).method_23763(matrices.method_23760().method_23762(), dx, dy, dz).method_1344();
         out.method_22918(matrix, (float)x2, (float)y2, (float)z2).method_22915(c.r, c.g, c.b, c.a).method_23763(matrices.method_23760().method_23762(), dx, dy, dz).method_1344();
      }
   }

   public boolean rendersOutsideBoundingBox(TileRegion tile) {
      return true;
   }
}
