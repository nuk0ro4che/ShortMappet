package mchorse.mappet.mixins;

import net.minecraft.class_1011;
import net.minecraft.class_765;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(class_765.class)
public interface LightmapTextureManagerAccessor {
   @Accessor("field_4133")
   class_1011 mappet$getLightmapImage();
}
