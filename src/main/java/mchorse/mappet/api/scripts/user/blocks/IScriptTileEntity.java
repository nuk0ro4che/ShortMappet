package mchorse.mappet.api.scripts.user.blocks;

import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import net.minecraft.class_2586;

public interface IScriptTileEntity {
   class_2586 getMinecraftBlockEntity();
   @Deprecated
   default class_2586 getMinecraftTileEntity() {
      return this.getMinecraftBlockEntity();
   }

   String getId();

   boolean isInvalid();

   INBTCompound getData();

   void setData(INBTCompound var1);

   INBTCompound getTileData();
}
