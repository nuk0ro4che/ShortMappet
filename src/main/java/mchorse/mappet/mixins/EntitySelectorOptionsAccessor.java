package mchorse.mappet.mixins;

import java.util.function.Predicate;
import net.minecraft.class_2303;
import net.minecraft.class_2306;
import net.minecraft.class_2561;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_2306.class})
public interface EntitySelectorOptionsAccessor {
   @Invoker("method_9961")
   static void mappet$putOption(String id, class_2306.class_2307 handler, Predicate<class_2303> condition, class_2561 description) {
      throw new AssertionError();
   }
}
