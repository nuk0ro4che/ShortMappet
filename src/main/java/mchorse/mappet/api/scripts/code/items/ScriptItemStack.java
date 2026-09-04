package mchorse.mappet.api.scripts.code.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mchorse.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import mchorse.mappet.api.scripts.user.items.IScriptItem;
import mchorse.mappet.api.scripts.user.items.IScriptItemStack;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.compat.NbtCompat;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2519;
import net.minecraft.class_2561;
import net.minecraft.class_2561.class_2562;

public class ScriptItemStack implements IScriptItemStack {
   public static final ScriptItemStack EMPTY;
   private static final String CAN_DESTROY = "CanDestroy";
   private static final String CAN_PLACE_ON = "CanPlaceOn";
   private class_1799 stack;
   private IScriptItem item;

   public static IScriptItemStack create(class_1799 stack) {
      return stack != null && !stack.method_7960() ? new ScriptItemStack(stack) : EMPTY;
   }

   private ScriptItemStack(class_1799 stack) {
      this.stack = stack;
   }

   public class_1799 getMinecraftItemStack() {
      return this.stack;
   }

   public boolean isEmpty() {
      return this.stack.method_7960();
   }

   public IScriptItemStack copy() {
      return new ScriptItemStack(this.getMinecraftItemStack().method_7972());
   }

   public IScriptItem getItem() {
      if (this.item == null) {
         this.item = new ScriptItem(this.stack.method_7909());
      }

      return this.item;
   }

   public int getMaxCount() {
      return this.stack.method_7914();
   }

   public int getCount() {
      return this.stack.method_7947();
   }

   public void setCount(int count) {
      this.stack.method_7939(count);
   }

   public int getMeta() {
      return this.stack.method_7919();
   }

   public void setMeta(int meta) {
      this.stack.method_7974(meta);
   }

   public boolean hasData() {
      return this.stack.method_7985();
   }

   public INBTCompound getData() {
      return new ScriptNBTCompound(this.stack.method_7969());
   }

   public void setData(INBTCompound tag) {
      this.stack.method_7980(tag.getNbtCompound());
   }

   public INBTCompound serialize() {
      return new ScriptNBTCompound(NbtCompat.write(this.stack));
   }

   public String getDisplayName() {
      return this.stack.method_7964().getString();
   }

   public void setDisplayName(String name) {
      this.stack.method_7977(class_2561.method_43470(name));
   }

   private class_2499 getLoreNBTList() {
      class_2487 tag = this.stack.method_7969();
      if (tag == null) {
         return null;
      } else {
         if (!tag.method_10573("display", 10)) {
            tag.method_10566("display", new class_2487());
         }

         class_2487 display = tag.method_10562("display");
         if (!display.method_10573("Lore", 9)) {
            display.method_10566("Lore", new class_2499());
         }

         return display.method_10554("Lore", 8);
      }
   }

   private static String encodeLore(String lore) {
      return class_2562.method_10867(class_2561.method_43470(lore));
   }

   private static String decodeLore(String lore) {
      try {
         class_2561 text = class_2562.method_10877(lore);
         return text == null ? lore : text.getString();
      } catch (Exception var2) {
         return lore;
      }
   }

   public String getLore(int index) {
      class_2499 list = this.getLoreNBTList();
      if (list != null && index < list.size()) {
         return decodeLore(list.method_10608(index));
      } else {
         throw new IllegalStateException("Lore index out of bounds, or no lore exists.");
      }
   }

   public List<String> getLoreList() {
      class_2499 lore = this.getLoreNBTList();
      if (lore == null) {
         return Collections.emptyList();
      } else {
         List<String> loreList = new ArrayList();

         for(int i = 0; i < lore.size(); ++i) {
            loreList.add(decodeLore(lore.method_10608(i)));
         }

         return loreList;
      }
   }

   public void setLore(int index, String string) {
      class_2499 lore = this.getLoreNBTList();
      if (lore != null && index >= 0 && index < lore.size()) {
         lore.method_10606(index, class_2519.method_23256(encodeLore(string)));
      } else {
         throw new IllegalStateException("Lore index out of bounds, or no lore exists.");
      }
   }

   public void addLore(String string) {
      class_2499 lore = this.getLoreNBTList();
      if (lore != null) {
         lore.add(class_2519.method_23256(encodeLore(string)));
      }

   }

   public void clearAllLores() {
      class_2499 lore = this.getLoreNBTList();
      if (lore != null) {
         while(lore.size() > 0) {
            lore.method_10536(lore.size() - 1);
         }
      }

   }

   public void clearLore(int index) {
      class_2499 lore = this.getLoreNBTList();
      if (lore != null && index >= 0 && index < lore.size()) {
         lore.method_10536(index);
      } else {
         throw new IllegalStateException("Lore index out of bounds, or no lore exists.");
      }
   }

   public void clearAllEnchantments() {
      class_2487 tag = this.stack.method_7969();
      if (tag != null) {
         tag.method_10551("ench");
      }

   }

   public List<String> getCanDestroyBlocks() {
      class_2487 tag = this.stack.method_7969();
      if (tag != null && tag.method_10573("CanDestroy", 9)) {
         List<String> canDestroyBlocks = new ArrayList();
         class_2499 list = tag.method_10554("CanDestroy", 8);

         for(int i = 0; i < list.size(); ++i) {
            canDestroyBlocks.add(list.method_10608(i));
         }

         return canDestroyBlocks;
      } else {
         return Collections.emptyList();
      }
   }

   public void addCanDestroyBlock(String block) {
      class_2487 tag = this.stack.method_7969();
      if (tag == null) {
         tag = new class_2487();
         this.stack.method_7980(tag);
      }

      class_2499 canDestroyList;
      if (!tag.method_10573("CanDestroy", 9)) {
         canDestroyList = new class_2499();
         tag.method_10566("CanDestroy", canDestroyList);
      } else {
         canDestroyList = tag.method_10554("CanDestroy", 8);
      }

      for(int i = 0; i < canDestroyList.size(); ++i) {
         if (canDestroyList.method_10608(i).equals(block)) {
            return;
         }
      }

      canDestroyList.add(class_2519.method_23256(block));
   }

   public void clearAllCanDestroyBlocks() {
      class_2487 tag = this.stack.method_7969();
      if (tag != null) {
         tag.method_10551("CanDestroy");
      }

   }

   public void clearCanDestroyBlock(String block) {
      class_2487 tag = this.stack.method_7969();
      if (tag != null) {
         class_2499 canPlaceOn = tag.method_10554("CanDestroy", 8);
         class_2499 newCanPlaceOn = new class_2499();

         for(int i = 0; i < canPlaceOn.size(); ++i) {
            if (!canPlaceOn.method_10608(i).equals(block)) {
               newCanPlaceOn.add(canPlaceOn.method_10534(i));
            }
         }

         tag.method_10566("CanDestroy", newCanPlaceOn);
      }

   }

   public List<String> getCanPlaceOnBlocks() {
      class_2487 tag = this.stack.method_7969();
      if (tag != null && tag.method_10573("CanPlaceOn", 9)) {
         List<String> canPlaceOn = new ArrayList();
         class_2499 list = tag.method_10554("CanPlaceOn", 8);

         for(int i = 0; i < list.size(); ++i) {
            canPlaceOn.add(list.method_10608(i));
         }

         return canPlaceOn;
      } else {
         return Collections.emptyList();
      }
   }

   public void addCanPlaceOnBlock(String block) {
      class_2487 tag = this.stack.method_7969();
      if (tag == null) {
         this.stack.method_7980(new class_2487());
      }

      if (!tag.method_10573("CanPlaceOn", 9)) {
         tag.method_10566("CanPlaceOn", new class_2499());
      }

      tag.method_10554("CanPlaceOn", 8).add(class_2519.method_23256(block));
   }

   public void clearAllCanPlaceOnBlocks() {
      class_2487 tag = this.stack.method_7969();
      if (tag != null) {
         tag.method_10551("CanPlaceOn");
      }

   }

   public void clearCanPlaceOnBlock(String block) {
      class_2487 tag = this.stack.method_7969();
      if (tag != null) {
         class_2499 canPlaceOn = tag.method_10554("CanPlaceOn", 8);
         class_2499 newCanPlaceOn = new class_2499();

         for(int i = 0; i < canPlaceOn.size(); ++i) {
            if (!canPlaceOn.method_10608(i).equals(block)) {
               newCanPlaceOn.add(canPlaceOn.method_10534(i));
            }
         }

         tag.method_10566("CanPlaceOn", newCanPlaceOn);
      }

   }

   public int getRepairCost() {
      return this.stack.method_7928();
   }

   public void setRepairCost(int cost) {
      this.stack.method_7927(cost);
   }

   public boolean isUnbreakable() {
      return !this.stack.method_7963();
   }

   public void setUnbreakable(boolean unbreakable) {
      class_2487 tag = this.stack.method_7969();
      if (tag != null) {
         tag.method_10556("Unbreakable", unbreakable);
      }

   }

   public void add(int amount) {
      int newCount = this.stack.method_7947() + amount;
      if (newCount <= 0) {
         this.stack.method_7934(this.stack.method_7947());
      } else {
         this.stack.method_7939(newCount);
      }

   }

   public boolean equals(ScriptItemStack other) {
      return class_1799.method_31577(this.stack, other.stack);
   }

   static {
      EMPTY = new ScriptItemStack(class_1799.field_8037);
   }
}
