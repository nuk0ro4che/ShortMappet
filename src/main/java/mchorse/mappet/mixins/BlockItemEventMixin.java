package mchorse.mappet.mixins;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_1269;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1750;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_638;
import net.minecraft.class_7923;
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
               } else if (event.hasPlaceOverride()) {
                  class_2338 pos = event.getFinalPos();
                  class_2680 overrideState = event.getFinalPlacedBlock();
                  Mappet.logInfo("OVERRIDE mixin placing " + class_7923.field_41175.method_10221(overrideState.method_26204()) + " at " + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260());
                  this.mappet$placeOverride(context, pos, overrideState, event);
                  cir.setReturnValue(class_1269.field_5811);
               }

            }
         }
      }
   }

   private void mappet$placeOverride(class_1750 context, class_2338 pos, class_2680 state, LegacyEvents.BlockEvent.PlaceEvent event) {
      class_1937 world = context.method_8045();
      boolean set = world.method_8652(pos, state, 11);
      Mappet.logInfo("OVERRIDE mixin placing " + class_7923.field_41175.method_10221(state.method_26204()) + " at " + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260() + " serverSet=" + set + " now=" + class_7923.field_41175.method_10221(world.method_8320(pos).method_26204()));
      class_2338 priorPos = event.getPos();
      if (priorPos != null && !priorPos.equals(pos)) {
         class_2680 current = world.method_8320(priorPos);
         world.method_8413(priorPos, current, current, 3);
         Mappet.logInfo("OVERRIDE mixin resynced prior pos " + priorPos.method_10263() + "," + priorPos.method_10264() + "," + priorPos.method_10260());
      }
      class_310 client = class_310.method_1551();
      class_638 cworld = client == null ? null : client.field_1687;
      if (cworld != null && cworld != world) {
         cworld.method_8652(pos, state, 3);
         if (client.field_1769 != null) {
            client.field_1769.method_18145(pos.method_10263(), pos.method_10264(), pos.method_10260());
         }
         if (priorPos != null && !priorPos.equals(pos)) {
            cworld.method_8652(priorPos, class_2246.field_10124.method_9564(), 3);
            if (client.field_1769 != null) {
               client.field_1769.method_18145(priorPos.method_10263(), priorPos.method_10264(), priorPos.method_10260());
            }
         }
         Mappet.logInfo("SP-MIRROR client placed " + class_7923.field_41175.method_10221(state.method_26204()) + " at " + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260());
      }
      class_1657 player = context.method_8036();
      if (player != null && !player.method_31549().field_7477) {
         class_1799 stack = context.method_8041();
         if (!stack.method_7960()) {
            stack.method_7934(1);
         }
      }

      if (world.method_8320(pos).method_26204() == state.method_26204()) {
         class_3414 sound = state.method_26231().method_10598();
         world.method_8396(player, pos, sound, class_3419.field_15245, 0.5F, 0.8F);
      }
   }
}