package mchorse.mappet.client.gui;

import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.client.gui.crafting.GuiCrafting;
import mchorse.mappet.client.gui.crafting.ICraftingScreen;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.crafting.PacketCraftingTable;
import mchorse.mclib.client.gui.framework.GuiBase;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class GuiCraftingTableScreen extends GuiBase implements ICraftingScreen {
   public GuiCrafting crafting;

   public GuiCraftingTableScreen(CraftingTable table) {
      class_310 mc = class_310.method_1551();
      this.crafting = new GuiCrafting(mc);
      this.crafting.set(table);
      this.crafting.flex().relative(this.viewport).y(20).w(1.0F).h(1.0F, -20);
      this.root.add(this.crafting);
   }

   public boolean method_25421() {
      return false;
   }

   public void refresh() {
      this.crafting.refresh();
   }

   protected void closeScreen() {
      super.closeScreen();
      Dispatcher.sendToServer(new PacketCraftingTable((CraftingTable)null));
   }

   public void method_25394(class_332 drawContext, int mouseX, int mouseY, float partialTicks) {
      this.method_25420(drawContext);
      super.method_25394(drawContext, mouseX, mouseY, partialTicks);
      GuiDraw.drawCenteredString(this.field_22793, this.crafting.get().title, this.viewport.mx(), 11, 16777215);
   }
}
