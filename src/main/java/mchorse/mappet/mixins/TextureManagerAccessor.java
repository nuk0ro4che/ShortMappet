package mchorse.mappet.mixins;

import java.util.Map;
import net.minecraft.class_1044;
import net.minecraft.class_1060;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(class_1060.class)
public interface TextureManagerAccessor {
   @Accessor("field_5286")
   Map<class_2960, class_1044> mappet$textures();
}