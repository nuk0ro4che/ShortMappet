package mchorse.mappet.mixins;

import net.minecraft.class_364;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_437.class)
public interface ScreenAccessor
{
    @Invoker("method_37063")
    class_364 mappet$addDrawableChild(class_364 widget);
}
