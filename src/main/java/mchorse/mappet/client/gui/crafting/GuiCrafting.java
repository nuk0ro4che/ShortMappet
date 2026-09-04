package mchorse.mappet.client.gui.crafting;

import java.util.Objects;
import mchorse.mappet.api.crafting.CraftingRecipe;
import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.crafting.PacketCraft;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.utils.Area;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.TextUtils;
import net.minecraft.class_310;
import net.minecraft.class_327;

public class GuiCrafting extends GuiElement implements ICraftingScreen {
   public static final IKey CRAFT_LABEL = IKey.lang("mappet.gui.crafting.craft");
   public GuiCraftingRecipes recipes;
   public GuiButtonElement craft;
   private CraftingTable table;

   public GuiCrafting(class_310 mc) {
      super(mc);
      this.craft = new GuiButtonElement(mc, CRAFT_LABEL, this::craft);
      this.craft.flex().relative(this.area).x(1.0F, -10).y(1.0F, -10).wh(80, 20).anchor(1.0F, 1.0F);
      this.recipes = new GuiCraftingRecipes(mc, (element) -> this.pickRecipe(element.getRecipe()));
      this.recipes.flex().relative(this.area).x(10).y(10).w(1.0F, -20).hTo(this.craft.area, -5);
      this.add(new IGuiElement[]{this.craft, this.recipes});
   }

   public CraftingTable get() {
      return this.table;
   }

   public void set(CraftingTable table) {
      this.table = table;
      this.craft.label = table.action.trim().isEmpty() ? CRAFT_LABEL : IKey.str(TextUtils.processColoredText(table.action));
      this.recipes.setTable(this.table);
      this.pickRecipe((CraftingRecipe)this.table.recipes.get(0));
      this.recipes.setRecipe((CraftingRecipe)this.table.recipes.get(0));
      this.keys().keybinds.clear();

      for(CraftingRecipe recipe : this.table.recipes) {
         if (recipe.hotkey > 0) {
            this.keys().register(IKey.format("mappet.gui.crafting.keys.craft", new Object[]{recipe.title}), recipe.hotkey, () -> {
               this.pickRecipe(recipe);
               this.recipes.setRecipe(recipe);
               this.craft(this.craft);
            });
         }
      }

   }

   public void refresh() {
      this.pickRecipe(this.recipes.getCurrent().getRecipe());
   }

   private void craft(GuiButtonElement button) {
      Dispatcher.sendToServer(new PacketCraft(this.recipes.getChildren().indexOf(this.recipes.getCurrent())));
   }

   private void pickRecipe(CraftingRecipe recipe) {
      this.craft.setEnabled(recipe.isPlayerHasAllItems(class_310.method_1551().field_1724));
   }

   public void draw(GuiContext context) {
      super.draw(context);
      if (this.mc.field_1724.method_7337() && this.table != null) {
         int w = this.font.method_1727(this.table.getId());
         class_327 var10000 = this.font;
         String var10001 = this.table.getId();
         int var10002 = this.area.mx(w);
         Area var10003 = this.craft.area;
         Objects.requireNonNull(this.font);
         GuiDraw.drawTextBackground(var10000, var10001, var10002, var10003.my(9 - 2), 16777215, -2013265920);
      }

   }

   public boolean mouseClicked(GuiContext context) {
      if (this.craft.area.isInside(context) && context.mouseButton == 0) {
         this.craft(this.craft);
         return true;
      } else {
         return super.mouseClicked(context);
      }
   }
}
