package mchorse.mappet.api.utils;

import mchorse.mappet.api.utils.manager.IManager;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.panels.GuiMappetDashboardPanel;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface IContentType {
   IManager<? extends AbstractData> getManager();

   @Environment(EnvType.CLIENT)
   GuiMappetDashboardPanel get(GuiMappetDashboard var1);

   @Environment(EnvType.CLIENT)
   IKey getPickLabel();

   String getName();

   static IContentType getType(String name) {
      IContentType type = ContentType.valueOf(name);
      return type;
   }
}
