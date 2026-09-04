package mchorse.mappet.mixins;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1657.class})
public class PlayerDropEventMixin {
   @Inject(
      method = {"method_7329(Lnet/minecraft/class_1799;ZZ)Lnet/minecraft/class_1542;"},
      at = {@At("RETURN")}
   )
   private void mappet$toss(class_1799 stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<class_1542> cir) {
      class_1657 player = (class_1657)(Object)this;
      class_1542 item = (class_1542)cir.getReturnValue();
      if (!player.method_37908().field_9236 && item != null) {
         LegacyEvents.ItemTossEvent event = new LegacyEvents.ItemTossEvent(player, item);
         CommonProxy.eventHandler.onPlayerToss(event);
         CommonProxy.scriptedItemEventHandler.onPlayerTossScriptedItem(event);
         if (event.isCanceled()) {
            item.method_31472();
         }
      }

   }
}
