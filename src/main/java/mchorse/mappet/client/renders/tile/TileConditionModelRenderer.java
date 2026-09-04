package mchorse.mappet.client.renders.tile;

import mchorse.blockbuster.Blockbuster;
import mchorse.blockbuster.common.tileentity.TileEntityModelSettings;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mclib.utils.MatrixUtils.RotationOrder;
import mchorse.metamorph.api.EntityUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.render.MorphRenderPipeline;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_761;
import net.minecraft.class_7833;
import net.minecraft.class_827;

public class TileConditionModelRenderer implements class_827<TileConditionModel> {
   public void method_3569(TileConditionModel tile, float tickDelta, class_4587 matrices, class_4597 consumers, int light, int overlay) {
      if (!(Boolean)Blockbuster.modelBlockDisableRendering.get()) {
         this.renderModel(tile, tickDelta, matrices, consumers, light);
      }

      class_310 client = class_310.method_1551();
      if (client.field_1690.field_1866 && (!client.field_1690.field_1842 || (Boolean)Blockbuster.modelBlockRenderDebuginf1.get())) {
         float r = tile.entity != null && !tile.entity.morph.isEmpty() && tile.entity.morph.get().errorRendering ? 1.0F : 0.0F;
         float g = r == 1.0F ? 0.0F : 1.0F;
         class_761.method_22980(matrices, consumers.getBuffer(class_1921.method_23594()), (double)0.25F, (double)0.25F, (double)0.25F, (double)0.75F, (double)0.75F, (double)0.75F, r, g, 0.5F, 0.8F);
      }

   }

   private void renderModel(TileConditionModel tile, float tickDelta, class_4587 matrices, class_4597 consumers, int light) {
      class_310 client = class_310.method_1551();
      if (client.field_1687 != null) {
         if (tile.entity == null) {
            tile.createEntity(client.field_1687);
         }

         class_1309 entity = tile.entity;
         AbstractMorph morph = EntityUtils.getMorph(entity);
         if (morph != null) {
            TileEntityModelSettings settings = tile.getSettings();
            entity.method_36456(0.0F);
            entity.field_5982 = 0.0F;
            entity.method_36457(0.0F);
            entity.field_6004 = 0.0F;
            entity.method_5847(0.0F);
            entity.field_6259 = 0.0F;
            entity.field_6283 = 0.0F;
            entity.field_6220 = 0.0F;
            entity.method_18800((double)0.0F, (double)0.0F, (double)0.0F);
            matrices.method_22903();
            matrices.method_46416(0.5F + settings.getX(), settings.getY(), 0.5F + settings.getZ());
            if (settings.getOrder() == RotationOrder.ZYX) {
               matrices.method_22907(class_7833.field_40714.rotationDegrees(settings.getRx()));
               matrices.method_22907(class_7833.field_40716.rotationDegrees(settings.getRy()));
               matrices.method_22907(class_7833.field_40718.rotationDegrees(settings.getRz()));
            } else {
               matrices.method_22907(class_7833.field_40718.rotationDegrees(settings.getRz()));
               matrices.method_22907(class_7833.field_40716.rotationDegrees(settings.getRy()));
               matrices.method_22907(class_7833.field_40714.rotationDegrees(settings.getRx()));
            }

            matrices.method_22905(settings.getSx(), settings.isUniform() ? settings.getSx() : settings.getSy(), settings.isUniform() ? settings.getSx() : settings.getSz());
            MorphRenderPipeline.drawEntity(morph, entity, matrices, consumers, light == 0 ? 15728880 : light, tickDelta);
            matrices.method_22909();
         }
      }
   }

   public boolean rendersOutsideBoundingBox(TileConditionModel tile) {
      return tile.isGlobal;
   }
}
