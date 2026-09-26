package mchorse.mappet.mixins;

import mchorse.mappet.utils.MappetBlockBreakSpeed;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * Scales the block breaking speed of a player via a configured multiplier.
 * 1.0 (no entry) is vanilla, 0 makes the block unbreakable.
 */
@Mixin(class_1657.class)
public abstract class PlayerEntityBlockBreakSpeedMixin {
    @Inject(method = {"method_7351"}, at = {@At("RETURN")}, cancellable = true)
    private void mappet$onBlockBreakingSpeed(class_2680 block, CallbackInfoReturnable<Float> cir) {
        UUID uuid = ((class_1297) (Object) this).method_5667();
        Float multiplier = MappetBlockBreakSpeed.get(uuid);

        if (multiplier != null && multiplier >= 0.0F) {
            cir.setReturnValue(cir.getReturnValueF() * multiplier);
        }
    }
}