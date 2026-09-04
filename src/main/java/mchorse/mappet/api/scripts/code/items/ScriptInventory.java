package mchorse.mappet.api.scripts.code.items;

import mchorse.mappet.api.scripts.user.items.IScriptInventory;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import net.minecraft.class_1263;
import net.minecraft.class_1275;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2624;

public class ScriptInventory implements IScriptInventory {
   private final class_1263 inventory;

   public ScriptInventory(class_1263 inventory) {
      this.inventory = inventory;
   }

   public class_1263 getMinecraftInventory() {
      return this.inventory;
   }

   public boolean isEmpty() {
      return this.inventory.method_5442();
   }

   public int size() {
      return this.inventory.method_5439();
   }

   public IScriptItemStack getStack(int index) {
      return ScriptItemStack.create(this.inventory.method_5438(index));
   }

   public IScriptItemStack removeStack(int index) {
      return ScriptItemStack.create(this.inventory.method_5441(index));
   }

   public void setStack(int index, IScriptItemStack stack) {
      if (index >= 0 && index < this.size()) {
         this.inventory.method_5447(index, stack == null ? class_1799.field_8037 : stack.getMinecraftItemStack());
         this.inventory.method_5431();
      }

   }

   public void clear() {
      this.inventory.method_5448();
      this.inventory.method_5431();
   }

   public String getName() {
      class_1263 var2 = this.inventory;
      String var10000;
      if (var2 instanceof class_1275 nameable) {
         var10000 = nameable.method_5477().getString();
      } else {
         var10000 = "container";
      }

      return var10000;
   }

   public boolean hasCustomName() {
      class_1263 var2 = this.inventory;
      boolean var10000;
      if (var2 instanceof class_1275 nameable) {
         if (nameable.method_16914()) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public void setName(String name) {
      class_1263 var3 = this.inventory;
      if (var3 instanceof class_2624 container) {
         container.method_17488(class_2561.method_43470(name));
         container.method_5431();
      }

   }
}
