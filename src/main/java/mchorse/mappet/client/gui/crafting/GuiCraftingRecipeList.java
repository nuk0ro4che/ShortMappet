package mchorse.mappet.client.gui.crafting;

import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.api.crafting.CraftingRecipe;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import net.minecraft.class_310;

public class GuiCraftingRecipeList extends GuiListElement<CraftingRecipe> {
   public GuiCraftingRecipeList(class_310 mc, Consumer<List<CraftingRecipe>> callback) {
      super(mc, callback);
   }

   protected String elementToString(CraftingRecipe element) {
      return element.title;
   }
}
