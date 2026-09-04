package mchorse.mappet.mixins;

import net.minecraft.class_312;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_312.class)
public interface MousePositionAccessor {
   @Accessor("field_1795")
   double mappet$getX();

   @Accessor("field_1795")
   void mappet$setX(double value);

   @Accessor("field_1794")
   double mappet$getY();

   @Accessor("field_1794")
   void mappet$setY(double value);
}
