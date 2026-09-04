package mchorse.mappet.mixins;

import net.minecraft.class_315;
import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_315.class})
public interface GameOptionsAccessor {
   @Accessor("field_1826")
   class_7172<Integer> mappet$getFov();

   @Accessor("field_1840")
   class_7172<Double> mappet$getGamma();

   @Accessor("field_1843")
   class_7172<Double> mappet$getMouseSensitivity();

   @Accessor("field_1870")
   class_7172<Integer> mappet$getViewDistance();

   @Accessor("field_1848")
   class_7172<Boolean> mappet$getAutoJump();

   @Accessor("field_1891")
   class_7172<Boolean> mappet$getViewBobbing();

   @Accessor("field_1888")
   class_7172<Boolean> mappet$getEntityShadows();
}
