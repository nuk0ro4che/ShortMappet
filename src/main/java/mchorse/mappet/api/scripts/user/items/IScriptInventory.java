package mchorse.mappet.api.scripts.user.items;

import net.minecraft.class_1263;

public interface IScriptInventory {
   class_1263 getMinecraftInventory();

   boolean isEmpty();

   int size();

   IScriptItemStack getStack(int var1);

   IScriptItemStack removeStack(int var1);

   void setStack(int var1, IScriptItemStack var2);

   void clear();

   String getName();

   boolean hasCustomName();

   void setName(String var1);
}
