package mchorse.mappet.api.npcs;

import mchorse.mappet.compat.INBTSerializable;
import mchorse.mclib.utils.MathUtils;
import net.minecraft.class_1799;
import net.minecraft.class_2487;

public class NpcDrop implements INBTSerializable<class_2487> {
   public class_1799 stack;
   public float chance;

   public NpcDrop() {
      this.stack = class_1799.field_8037;
      this.chance = 1.0F;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10566("Stack", this.stack.method_7953(new class_2487()));
      tag.method_10548("Chance", this.chance);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Stack")) {
         this.stack = class_1799.method_7915(tag.method_10562("Stack"));
      }

      if (tag.method_10545("Chance")) {
         this.chance = MathUtils.clamp(tag.method_10583("Chance"), 0.0F, 1.0F);
      }

   }
}
