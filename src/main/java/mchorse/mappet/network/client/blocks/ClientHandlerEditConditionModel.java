package mchorse.mappet.network.client.blocks;

import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.network.common.blocks.PacketEditConditionModel;
import mchorse.mappet.tile.TileConditionModel;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.client.gui.mclib.GuiDashboardPanel;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2586;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_746;

public class ClientHandlerEditConditionModel extends ClientMessageHandler<PacketEditConditionModel> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketEditConditionModel message) {
      class_2586 tile = WorldUtils.getBlockEntity(player.method_37908(), message.pos);
      if (tile instanceof TileConditionModel tileConditionModel) {
         GuiMappetDashboard dashboard = GuiMappetDashboard.get(class_310.method_1551());
         class_437 screen = class_310.method_1551().field_1755;
         if (message.isEdit) {
            tileConditionModel.fill(message.tag);
            dashboard.panels.setPanel(dashboard.conditionModel);
            dashboard.conditionModel.fill(tileConditionModel, true);
            class_310.method_1551().method_1507(dashboard);
         } else if (!dashboard.equals(screen) || !((GuiDashboardPanel)dashboard.panels.view.delegate).equals(dashboard.conditionModel)) {
            AbstractMorph morph = MorphManager.INSTANCE.morphFromNBT(message.tag.method_10562("morph"));
            tileConditionModel.isGlobal = message.tag.method_10577("global");
            tileConditionModel.isShadow = message.tag.method_10577("shadow");
            if (tileConditionModel.entity == null) {
               tileConditionModel.createEntity(player.method_37908());
            }

            if (tileConditionModel.entity.morph.get() == null || !tileConditionModel.entity.morph.get().equals(morph)) {
               tileConditionModel.entity.morph.set((AbstractMorph)null);
               tileConditionModel.entity.morph(morph, true);
               tileConditionModel.entity.field_6012 = 0;
            }
         }

      }
   }
}
