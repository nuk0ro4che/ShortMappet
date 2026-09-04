package mchorse.mappet.compat;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.class_2487;
import net.minecraft.class_2586;

public final class BlockEntityData {
   private static final Map<class_2586, class_2487> DATA = Collections.synchronizedMap(new WeakHashMap());

   private BlockEntityData() {
   }

   public static class_2487 get(class_2586 blockEntity) {
      return (class_2487)DATA.computeIfAbsent(blockEntity, (ignored) -> new class_2487());
   }
}
