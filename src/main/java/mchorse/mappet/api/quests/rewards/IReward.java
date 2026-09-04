package mchorse.mappet.api.quests.rewards;

import mchorse.mappet.compat.INBTSerializable;
import mchorse.mclib.network.IMessage;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_2487;

public interface IReward extends INBTSerializable<class_2487>, IMessage {
   static IReward fromType(String type) {
      return type.equals("item") ? new ItemStackReward(new class_1799[0]) : null;
   }

   void reward(class_1657 var1);

   IReward copy();

   String getType();
}
