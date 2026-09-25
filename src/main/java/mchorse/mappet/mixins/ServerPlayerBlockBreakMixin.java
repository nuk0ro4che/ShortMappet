package mchorse.mappet.mixins;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3225;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_3225.class})
public abstract class ServerPlayerBlockBreakMixin {
   @Shadow
   protected class_3218 field_14007;

   @Shadow
   @Final
   protected class_3222 field_14008;

   @Inject(
      method = {"method_14266"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$beforeBreak(class_2338 pos, CallbackInfoReturnable<Boolean> cir) {
      class_2680 original = this.field_14007.method_8320(pos);
      if (original == null || original.method_26204() == class_2246.field_10124) {
         return;
      }

      LegacyEvents.BlockEvent.BreakEvent event = new LegacyEvents.BlockEvent.BreakEvent(this.field_14007, pos, original, this.field_14008);
      CommonProxy.eventHandler.onPlayerBreakBlock(event);
      CommonProxy.scriptedItemEventHandler.onPlayerWithScriptedItemBreakBlock(event);
      if (event.isCanceled()) {
         mchorse.mappet.Mappet.logger.info("MIXIN-BREAK canceled at " + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260());
         cir.setReturnValue(false);
         return;
      }

      class_2680 override = event.getBrokenBlockOverride();
      if (override != null) {
         this.field_14007.method_8652(pos, override, 3);
         mchorse.mappet.Mappet.logger.info("MIXIN-BREAK swapped at " + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260() + " -> " + class_7923.field_41175.method_10221(override.method_26204()));
      }
   }
}