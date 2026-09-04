package mchorse.mappet.client.gui.nodes.dialogues;

import mchorse.mappet.api.dialogues.nodes.CraftingNode;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.nodes.GuiEventBaseNodePanel;
import mchorse.mappet.client.gui.utils.GuiMappetUtils;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiCraftingNodePanel extends GuiEventBaseNodePanel<CraftingNode> {
   public GuiButtonElement crafting;

   public GuiCraftingNodePanel(class_310 mc) {
      super(mc);
      this.crafting = new GuiButtonElement(mc, IKey.lang("mappet.gui.overlays.crafting"), (b) -> this.openCraftingTables());
      this.add(this.crafting);
   }

   private void openCraftingTables() {
      GuiMappetUtils.openPicker(ContentType.CRAFTING_TABLE, (this.node).table, (name) -> (this.node).table = name);
   }
}
