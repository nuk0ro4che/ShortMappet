package mchorse.mappet.api.quests.objectives;

import mchorse.mappet.api.quests.INBTPartialSerializable;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mclib.utils.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_2487;

public abstract class AbstractObjective implements INBTSerializable<class_2487>, INBTPartialSerializable {
   public String message = "";

   public static AbstractObjective fromType(String type) {
      if (type.equals("collect")) {
         return new CollectObjective();
      } else if (type.equals("kill")) {
         return new KillObjective();
      } else {
         return type.equals("state") ? new StateObjective() : null;
      }
   }

   public void initiate(class_1657 player) {
   }

   public abstract boolean isComplete(class_1657 var1);

   public abstract void complete(class_1657 var1);

   @Environment(EnvType.CLIENT)
   public String stringify(class_1657 player) {
      return TextUtils.processColoredText(this.stringifyObjective(player));
   }

   @Environment(EnvType.CLIENT)
   protected abstract String stringifyObjective(class_1657 var1);

   public abstract String getType();

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10582("Message", this.message);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Message")) {
         this.message = tag.method_10558("Message");
      }

   }
}
