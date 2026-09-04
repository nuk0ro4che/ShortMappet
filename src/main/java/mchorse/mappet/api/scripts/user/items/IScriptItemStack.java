package mchorse.mappet.api.scripts.user.items;

import java.util.List;
import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import net.minecraft.class_1799;

public interface IScriptItemStack {
   class_1799 getMinecraftItemStack();

   boolean isEmpty();

   IScriptItem getItem();

   IScriptItemStack copy();

   int getMaxCount();

   int getCount();

   void setCount(int var1);

   int getMeta();

   void setMeta(int var1);

   boolean hasData();

   INBTCompound getData();

   void setData(INBTCompound var1);

   INBTCompound serialize();

   String getDisplayName();

   void setDisplayName(String var1);

   String getLore(int var1);

   List<String> getLoreList();

   void setLore(int var1, String var2);

   void addLore(String var1);

   void clearAllLores();

   void clearLore(int var1);

   void clearAllEnchantments();

   List<String> getCanDestroyBlocks();

   void addCanDestroyBlock(String var1);

   void clearAllCanDestroyBlocks();

   void clearCanDestroyBlock(String var1);

   List<String> getCanPlaceOnBlocks();

   void addCanPlaceOnBlock(String var1);

   void clearAllCanPlaceOnBlocks();

   void clearCanPlaceOnBlock(String var1);

   int getRepairCost();

   void setRepairCost(int var1);

   boolean isUnbreakable();

   void setUnbreakable(boolean var1);

   void add(int var1);

   boolean equals(ScriptItemStack var1);
}
