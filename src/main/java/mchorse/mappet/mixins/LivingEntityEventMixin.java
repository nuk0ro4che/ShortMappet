package mchorse.mappet.mixins;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_1268;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1309.class})
public abstract class LivingEntityEventMixin {
   private class_1309 mappet$self() {
      return (class_1309)(Object)this;
   }


   @Inject(
      method = {"method_5643"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$attack(class_1282 source, float amount, CallbackInfoReturnable<Boolean> cir) {
      if (!this.mappet$self().method_37908().field_9236) {
         LegacyEvents.LivingAttackEvent event = new LegacyEvents.LivingAttackEvent(this.mappet$self(), source, amount);
         CommonProxy.eventHandler.onEntityAttacked(event);
         CommonProxy.scriptedItemEventHandler.onEntityAttackedWithScriptedItem(event);
         if (event.isCanceled()) {
            cir.setReturnValue(false);
         }

      }
   }

   @Inject(
      method = {"method_6074"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$damage(class_1282 source, float amount, CallbackInfo ci) {
      if (!this.mappet$self().method_37908().field_9236) {
         LegacyEvents.LivingDamageEvent event = new LegacyEvents.LivingDamageEvent(this.mappet$self(), source, amount);
         CommonProxy.eventHandler.onEntityHurt(event);
         if (event.isCanceled()) {
            ci.cancel();
         }

      }
   }

    @Inject(
       method = {"method_6078"},
       at = {@At("HEAD")},
       cancellable = true
    )
    private void mappet$death(class_1282 source, CallbackInfo ci) {
       if (!this.mappet$self().method_37908().field_9236 && !(this.mappet$self() instanceof class_1657)) {
          LegacyEvents.LivingDeathEvent event = new LegacyEvents.LivingDeathEvent(this.mappet$self(), source);
          CommonProxy.eventHandler.onMobKilled(event);
          if (event.isCanceled()) {
             ci.cancel();
          }
       }

    }

   @Inject(
      method = {"method_6005"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$knockback(double strength, double x, double z, CallbackInfo ci) {
      if (!this.mappet$self().method_37908().field_9236) {
         LegacyEvents.LivingKnockBackEvent event = new LegacyEvents.LivingKnockBackEvent(this.mappet$self(), (float)strength, (float)x, (float)z);
         CommonProxy.eventHandler.onLivingKnockBack(event);
         if (event.isCanceled()) {
            ci.cancel();
         }

      }
   }

   @Inject(
      method = {"method_6116"},
      at = {@At("HEAD")}
   )
   private void mappet$equipment(class_1304 slot, class_1799 oldStack, class_1799 newStack, CallbackInfo ci) {
      if (!this.mappet$self().method_37908().field_9236 && !class_1799.method_7973(oldStack, newStack)) {
         LegacyEvents.LivingEquipmentChangeEvent event = new LegacyEvents.LivingEquipmentChangeEvent(this.mappet$self(), slot, oldStack.method_7972(), newStack.method_7972());
         CommonProxy.eventHandler.onLivingEquipmentChange(event);
         CommonProxy.scriptedItemEventHandler.onLivingEquipmentChange(event);
      }
   }

   @Inject(
      method = {"method_6019"},
      at = {@At("TAIL")}
   )
   private void mappet$useStart(class_1268 hand, CallbackInfo ci) {
      if (!this.mappet$self().method_37908().field_9236) {
         CommonProxy.scriptedItemEventHandler.onScriptedItemUseStart(new LegacyEvents.LivingEntityUseItemEvent.Start(this.mappet$self(), this.mappet$self().method_6030(), this.mappet$self().method_6014()));
      }

   }

   @Inject(
      method = {"method_6075"},
      at = {@At("HEAD")}
   )
   private void mappet$useStop(CallbackInfo ci) {
      if (!this.mappet$self().method_37908().field_9236 && !this.mappet$self().method_6030().method_7960()) {
         CommonProxy.scriptedItemEventHandler.onScriptedItemUseStop(new LegacyEvents.LivingEntityUseItemEvent.Stop(this.mappet$self(), this.mappet$self().method_6030(), this.mappet$self().method_6014()));
      }

   }

   @Inject(
      method = {"method_6076"},
      at = {@At("HEAD")}
   )
   private void mappet$useTick(CallbackInfo ci) {
      if (!this.mappet$self().method_37908().field_9236 && !this.mappet$self().method_6030().method_7960()) {
         CommonProxy.scriptedItemEventHandler.onScriptedItemUseTick(new LegacyEvents.LivingEntityUseItemEvent.Tick(this.mappet$self(), this.mappet$self().method_6030(), this.mappet$self().method_6014()));
      }

   }

   @Inject(
      method = {"method_6040"},
      at = {@At("HEAD")}
   )
   private void mappet$useFinish(CallbackInfo ci) {
      if (!this.mappet$self().method_37908().field_9236 && !this.mappet$self().method_6030().method_7960()) {
         CommonProxy.scriptedItemEventHandler.onScriptedItemUseFinish(new LegacyEvents.LivingEntityUseItemEvent.Finish(this.mappet$self(), this.mappet$self().method_6030().method_7972(), this.mappet$self().method_6014()));
      }

   }
}
