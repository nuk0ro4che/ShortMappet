package mchorse.mappet.compat;

import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_2487;
import net.minecraft.class_2586;

public final class NbtCompat {
   private NbtCompat() {
   }

   public static class_2487 write(class_1799 stack) {
      return stack.method_7953(new class_2487());
   }

   public static class_2487 write(class_2586 blockEntity) {
      return blockEntity.method_38244();
   }

   public static class_2487 write(class_1297 entity) {
      class_2487 tag = new class_2487();
      entity.method_5647(tag);
      return tag;
   }
}
