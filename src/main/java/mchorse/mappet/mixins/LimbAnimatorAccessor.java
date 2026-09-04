package mchorse.mappet.mixins;

import net.minecraft.class_8080;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(class_8080.class)
public interface LimbAnimatorAccessor
{
    @Accessor("field_42109")
    float mappet$getPreviousSpeed();

    @Accessor("field_42109")
    void mappet$setPreviousSpeed(float value);

    @Accessor("field_42110")
    float mappet$getSpeed();

    @Accessor("field_42110")
    void mappet$setSpeed(float value);

    @Accessor("field_42111")
    float mappet$getPosition();

    @Accessor("field_42111")
    void mappet$setPosition(float value);
}
