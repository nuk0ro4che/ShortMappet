package mchorse.mappet.api.scripts.code.blocks;

import mchorse.mappet.api.scripts.code.nbt.ScriptNBTCompound;
import mchorse.mappet.api.scripts.user.blocks.IScriptTileEntity;
import mchorse.mappet.api.scripts.user.nbt.INBTCompound;
import mchorse.mappet.compat.BlockEntityData;
import mchorse.mappet.compat.NbtCompat;
import net.minecraft.class_2586;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class ScriptTileEntity implements IScriptTileEntity {
   private class_2586 tile;

   public ScriptTileEntity(class_2586 tile) {
      this.tile = tile;
   }

   public class_2586 getMinecraftBlockEntity() {
      return this.tile;
   }

   public String getId() {
      class_2960 key = class_7923.field_41181.method_10221(this.tile.method_11017());
      return key == null ? "" : key.toString();
   }

   public boolean isInvalid() {
      return this.tile.method_11015();
   }

   public INBTCompound getData() {
      return new ScriptNBTCompound(NbtCompat.write(this.tile));
   }

   public void setData(INBTCompound compound) {
      this.tile.method_11014(compound.getNbtCompound());
      this.tile.method_5431();
   }

   public INBTCompound getTileData() {
      return new ScriptNBTCompound(BlockEntityData.get(this.tile));
   }
}
