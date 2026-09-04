package mchorse.mappet.api.scripts.user.blocks;

import mchorse.mappet.api.scripts.user.IScriptWorld;
import net.minecraft.class_2680;

public interface IScriptBlockState {
   class_2680 getMinecraftBlockState();

   String getBlockId();

   int getMeta();

   boolean isSame(IScriptBlockState var1);

   boolean isSameBlock(IScriptBlockState var1);

   boolean isOpaque();

   boolean hasCollision(IScriptWorld var1, int var2, int var3, int var4);

   boolean isAir();
}
