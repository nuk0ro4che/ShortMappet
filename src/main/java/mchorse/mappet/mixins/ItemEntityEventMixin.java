package mchorse.mappet.mixins;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.EntityData;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1542.class})
public class ItemEntityEventMixin {
   @ModifyConstant(
      method = {"method_5773"},
      constant = {@Constant(
   intValue = 6000
)},
      require = 0
   )
   private int mappet$customLifespan(int vanillaLifespan) {
      class_1542 self = (class_1542)(Object)this;
      return EntityData.get(self).method_10545("Lifespan") ? Math.max(0, EntityData.get(self).method_10550("Lifespan")) : vanillaLifespan;
   }

   @Inject(
      method = {"method_5694"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$pickup(class_1657 player, CallbackInfo ci) {
      if (!player.method_37908().field_9236) {
         LegacyEvents.ItemEntityPickupEvent event = new LegacyEvents.ItemEntityPickupEvent(player, (class_1542)(Object)this);
         CommonProxy.eventHandler.onPlayerPickUp(event);
         CommonProxy.scriptedItemEventHandler.onFirstItemPickup(event);
         CommonProxy.scriptedItemEventHandler.onPlayerPickUpScriptedItem(event);
         if (event.isCanceled()) {
            ci.cancel();
         }

      }
   }
}
