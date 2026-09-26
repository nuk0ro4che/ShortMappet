package mchorse.mappet.mixins;

import mchorse.mappet.utils.MappetSprintSpeed;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replaces the vanilla sprint speed boost (x1.3) with a custom absolute speed
 * configured through {@code player.setSprintSpeed(speed)}.
 *
 * <ul>
 *     <li>No override &mdash; vanilla behaviour.</li>
 *     <li>{@code speed == 0} &mdash; sprinting moves at walk speed (boost removed).</li>
 *     <li>{@code speed > 0} &mdash; sprinting moves at the given absolute speed.</li>
 * </ul>
 */
@Mixin(class_1309.class)
public abstract class LivingEntitySprintSpeedMixin {
    @Inject(method = {"method_5728"}, at = {@At("RETURN")})
    private void mappet$onSprintToggle(boolean sprinting, CallbackInfo ci) {
        MappetSprintSpeed.sync((class_1309) (Object) this);
    }
}