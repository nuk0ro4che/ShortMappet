package mchorse.mappet.api.scripts.user.entities;

import mchorse.mappet.api.scripts.user.items.IScriptItemStack;

public interface IScriptEntityItem extends IScriptEntity {
   int getAge();

   void setAge(int var1);

   int getPickupDelay();

   void setPickupDelay(int var1);

   int getLifespan();

   void setLifespan(int var1);

   String getOwner();

   void setOwner(String var1);

   String getThrower();

   void setThrower(String var1);

   IScriptItemStack getItem();

   void setItem(IScriptItemStack var1);

   void setInfinitePickupDelay();

   void setDefaultPickupDelay();

   void setNoDespawn();

   boolean canPickup();
}
