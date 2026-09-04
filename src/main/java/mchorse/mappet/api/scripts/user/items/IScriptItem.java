package mchorse.mappet.api.scripts.user.items;

import net.minecraft.class_1792;

public interface IScriptItem {
   class_1792 getMinecraftItem();

   String getId();

   boolean isSame(IScriptItem var1);
}
