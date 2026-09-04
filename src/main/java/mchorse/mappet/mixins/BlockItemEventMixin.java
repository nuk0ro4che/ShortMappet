package mchorse.mappet.mixins;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_1269;
import net.minecraft.class_1747;
import net.minecraft.class_1750;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1747.class})
public abstract class BlockItemEventMixin {
   @Shadow
   public abstract class_1750 method_16356(class_1750 var1);

   @Shadow
   protected abstract class_2680 method_7707(class_1750 var1);

   @Shadow
   protected abstract boolean method_7709(class_1750 var1, class_2680 var2);

   @Inject(
      method = {"method_7712"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$beforePlace(class_1750 originalContext, CallbackInfoReturnable<class_1269> cir) {
      if (!originalContext.method_8045().field_9236 && originalContext.method_8036() != null && originalContext.method_7716()) {
         class_1750 context = this.method_16356(originalContext);
         if (context != null) {
            class_2680 state = this.method_7707(context);
            if (state != null && this.method_7709(context, state)) {
               LegacyEvents.BlockEvent.PlaceEvent event = new LegacyEvents.BlockEvent.PlaceEvent(context.method_8045(), context.method_8037(), state, context.method_8036());
               CommonProxy.eventHandler.onPlayerPlaceBlock(event);
               CommonProxy.scriptedItemEventHandler.onPlayerWithScriptedItemPlaceBlock(event);
               if (event.isCanceled()) {
                  cir.setReturnValue(class_1269.field_5814);
               }

            }
         }
      }
   }
}
