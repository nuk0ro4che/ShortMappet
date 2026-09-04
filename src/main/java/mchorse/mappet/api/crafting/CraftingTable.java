package mchorse.mappet.api.crafting;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.utils.AbstractData;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_3222;

public class CraftingTable extends AbstractData {
   public String title = "";
   public String action = "";
   public List<CraftingRecipe> recipes = new ArrayList();

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 recipes = new class_2499();

      for(CraftingRecipe recipe : this.recipes) {
         class_2487 recipeTag = recipe.serializeNBT();
         if (recipeTag.method_10546() > 0) {
            recipes.add(recipeTag);
         }
      }

      if (!this.title.isEmpty()) {
         tag.method_10582("Title", this.title);
      }

      if (!this.action.isEmpty()) {
         tag.method_10582("Action", this.action);
      }

      if (recipes.size() > 0) {
         tag.method_10566("Recipes", recipes);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Title")) {
         this.title = tag.method_10558("Title");
      }

      if (tag.method_10545("Action")) {
         this.action = tag.method_10558("Action");
      }

      if (tag.method_10545("Recipes")) {
         class_2499 recipes = tag.method_10554("Recipes", 10);

         for(int i = 0; i < recipes.size(); ++i) {
            CraftingRecipe recipe = new CraftingRecipe();
            recipe.deserializeNBT(recipes.method_10602(i));
            this.recipes.add(recipe);
         }
      }

   }

   public void filter(class_3222 player) {
      this.recipes.removeIf((recipe) -> !recipe.isAvailable(player));
   }
}
