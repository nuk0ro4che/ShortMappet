package mchorse.mappet.mixins;

import mchorse.mappet.client.HudRawCapture;
import net.minecraft.class_287;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Low level capture of every vertex batch built straight on the Tesselator.
 * Forge-style HUD blits (like {@code GuiGraphics.blit}) never touch the
 * {@link net.minecraft.class_332}, but they do end up here: the position is
 * written as the three first floats of the vertex element at offset 0, and by
 * the time the last component is written it can be measured and rewritten
 * right inside the vertex buffer.
 */
@Mixin(class_287.class)
public abstract class BufferBuilderMixin {
   @Inject(method = "method_22897", at = @At("HEAD"))
   private void mappet$putFloat(int index, float value, CallbackInfo ci) {
      HudRawCapture.floatAt((BufferBuilderAccessor)(Object)this, index, value);
   }

   @Inject(method = "method_43579", at = @At("RETURN"))
   private void mappet$endBatch(CallbackInfo ci) {
      HudRawCapture.endBatch();
   }
}