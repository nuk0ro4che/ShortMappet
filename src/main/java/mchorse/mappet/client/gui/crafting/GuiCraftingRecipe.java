package mchorse.mappet.client.gui.crafting;

import java.util.function.Consumer;
import mchorse.mappet.api.crafting.CraftingRecipe;
import mchorse.mappet.client.gui.utils.text.GuiText;
import mchorse.mappet.utils.MPIcons;
import mchorse.mclib.McLib;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiSlotElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.TextUtils;
import net.minecraft.class_1799;
import net.minecraft.class_2371;
import net.minecraft.class_310;

public class GuiCraftingRecipe extends GuiElement {
   private CraftingRecipe recipe;
   private GuiCraftingRecipes recipes;

   public GuiCraftingRecipe(class_310 mc, GuiCraftingRecipes recipes, CraftingRecipe recipe) {
      super(mc);
      this.recipe = recipe;
      this.recipes = recipes;
      GuiIconElement in = new GuiIconElement(mc, MPIcons.IN, (Consumer)null);
      GuiIconElement out = new GuiIconElement(mc, MPIcons.OUT, (Consumer)null);
      in.setEnabled(false);
      in.disabledColor = -1;
      out.setEnabled(false);
      out.disabledColor = -1;
      GuiElement output = this.createItems(mc, recipe.output);
      GuiElement column = Elements.column(mc, 4, new GuiElement[]{Elements.label(IKey.str(TextUtils.processColoredText(recipe.title)))});
      if (!recipe.description.trim().isEmpty()) {
         column.add((new GuiText(mc)).text(TextUtils.processColoredText(recipe.description)).color(11184810, true).marginTop(4));
      }

      if (!recipe.input.isEmpty()) {
         column.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.crafting.input")).marginTop(12), this.createItems(mc, recipe.input)});
      }

      output.flex().w(recipe.output.size() > 1 ? 44 : 20);
      this.add(Elements.row(mc, 5, new GuiElement[]{column, output}));
      this.flex().column(4).vertical().stretch().padding(10);
   }

   private GuiElement createItems(class_310 mc, class_2371<class_1799> input) {
      GuiElement element = new GuiElement(mc);

      for(class_1799 stack : input) {
         GuiSlotElement slot = new GuiSlotElement(mc, 0, (Consumer)null);
         slot.drawDisabled = false;
         slot.setStack(stack);
         slot.setEnabled(false);
         slot.flex().wh(20, 20);
         element.add(slot);
      }

      element.flex().grid(4).width(20);
      return element;
   }

   public CraftingRecipe getRecipe() {
      return this.recipe;
   }

   public boolean mouseClicked(GuiContext context) {
      if (super.mouseClicked(context)) {
         return true;
      } else if (this.area.isInside(context) && context.mouseButton == 0) {
         this.recipes.recipeClicked(this);
         return true;
      } else {
         return false;
      }
   }

   public void draw(GuiContext context) {
      if (this.recipes.getCurrent() == this) {
         this.area.draw(-2013265920 + (Integer)McLib.primaryColor.get());
      }

      int y = this.area.ey();
      GuiDraw.drawRect(this.area.x, y - 1, this.area.ex(), y, -2013265920);
      super.draw(context);
   }
}
