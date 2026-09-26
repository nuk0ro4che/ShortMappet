package mchorse.mappet.mixins;

import mchorse.mappet.CommonProxy;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.CharacterHolder;
import mchorse.mappet.compat.EntityDataHolder;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_1282;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1657.class})
public abstract class PlayerEntityMixin implements CharacterHolder {
   @Unique
   private final Character mappet$character = new Character();
   @Unique
   private LegacyEvents.LivingDamageEvent mappet$lastDamageEvent;

    public Character mappet$getCharacter() {
       return this.mappet$character;
    }

@Inject(
        method = {"method_5643"},
        at = {@At("HEAD")},
        cancellable = true
     )
    private void mappet$fullDamage(class_1282 source, float amount, CallbackInfoReturnable<Boolean> cir) {
       class_1657 player = (class_1657)(Object)this;
       if (!player.method_37908().field_9236) {
          LegacyEvents.LivingDamageEvent event = new LegacyEvents.LivingDamageEvent(player, source, amount);
          this.mappet$lastDamageEvent = event;
          CommonProxy.eventHandler.onEntityHurt(event);
          if (event.isCanceled()) {
             cir.setReturnValue(false);
          }
       }
    }

    @ModifyVariable(
       method = {"method_6074"},
       at = @At("HEAD"),
       argsOnly = true
    )
    private float mappet$modifiedDamage(float amount) {
       LegacyEvents.LivingDamageEvent event = this.mappet$lastDamageEvent;
       if (event != null) {
          float modifiedAmount = Math.max(0.0F, event.getAmount());
          this.mappet$lastDamageEvent = null;
          return modifiedAmount;
       }
       return amount;
    }

    @Inject(
       method = {"method_5693"},
       at = {@At("TAIL")}
    )
    private void mappet$initLayTracker(CallbackInfo ci) {
      class_1657 player = (class_1657)(Object)this;
      player.method_5841().method_12784(EntityDataHolder.MAPPET_LAY, false);
   }


   @Inject(
      method = {"method_5652"},
      at = {@At("TAIL")}
   )
   private void mappet$writeCharacter(class_2487 nbt, CallbackInfo ci) {
      nbt.method_10566("MappetCharacter", this.mappet$character.serializeNBT());
   }

   @Inject(
      method = {"method_5749"},
      at = {@At("TAIL")}
   )
   private void mappet$readCharacter(class_2487 nbt, CallbackInfo ci) {
      if (nbt.method_10545("MappetCharacter")) {
         this.mappet$character.deserializeNBT(nbt.method_10562("MappetCharacter"));
      }

   }
}
