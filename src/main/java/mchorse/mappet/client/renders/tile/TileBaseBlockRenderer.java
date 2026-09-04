package mchorse.mappet.client.renders.tile;

import mchorse.mclib.utils.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1921;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_761;
import net.minecraft.class_827;

@Environment(EnvType.CLIENT)
public abstract class TileBaseBlockRenderer<T extends class_2586> implements class_827<T> {
   protected final Color color;

   protected TileBaseBlockRenderer(Color color) {
      this.color = color;
   }

   public void method_3569(T tile, float tickDelta, class_4587 matrices, class_4597 consumers, int light, int overlay) {
      class_310 client = class_310.method_1551();
      if (this.canRender(client, tile)) {
         Color color = this.getBoxColor(tile);
         class_4588 lines = consumers.getBuffer(class_1921.method_23594());
         class_761.method_22980(matrices, lines, (double)0.25F, (double)0.25F, (double)0.25F, (double)0.75F, (double)0.75F, (double)0.75F, color.r, color.g, color.b, color.a);
         this.renderMoreDebug(tile, tickDelta, matrices, consumers, light, overlay);
      }
   }

   protected Color getBoxColor(T tile) {
      return this.color;
   }

   protected boolean canRender(class_310 client, T tile) {
      return client.field_1690.field_1866 && !client.field_1690.field_1842 && client.field_1724 != null && client.field_1724.method_7337();
   }

   protected void renderMoreDebug(T tile, float tickDelta, class_4587 matrices, class_4597 consumers, int light, int overlay) {
   }
}
