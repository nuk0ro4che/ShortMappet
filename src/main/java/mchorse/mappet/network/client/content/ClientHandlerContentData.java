package mchorse.mappet.network.client.content;

import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.api.ui.UIFile;
import mchorse.mappet.api.npcs.Npc;
import mchorse.mappet.api.scripts.Script;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mappet.network.common.content.PacketContentData;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_746;

public class ClientHandlerContentData extends ClientMessageHandler<PacketContentData> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketContentData message) {
      class_437 screen = class_310.method_1551().field_1755;
      if (screen instanceof GuiMappetDashboard dashboard) {
         GuiMappetDashboardPanel panel = message.type.get(dashboard);
         if (panel != null && panel.acceptsData(message.name)) {
            AbstractData data;

            if (message.type == ContentType.SCRIPTS || message.type == ContentType.CLIENT_SCRIPTS) {
               Script script = new Script();
               script.setId(message.name);
               script.deserializeNBT(message.data);
               data = script;
            } else {
               if (message.type.getManager() != null) {
                  data = (AbstractData)message.type.getManager().create(message.name, message.data);
               } else if (message.type == ContentType.NPC) {
                  data = new Npc();
                  data.setId(message.name);
                  data.deserializeNBT(message.data);
               } else if (message.type == ContentType.HUDS) {
                  data = new HUDScene();
                  data.setId(message.name);
                  data.deserializeNBT(message.data);
               } else if (message.type == ContentType.UIS) {
                  data = new UIFile();
                  data.setId(message.name);
                  data.deserializeNBT(message.data);
               } else {
                  return;
               }
            }

            panel.fill(data, message.allowed);
         }
      }

   }
}
