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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1657.class})
public abstract class PlayerEntityMixin implements CharacterHolder {
   @Unique
   private final Character mappet$character = new Character();

    public Character mappet$getCharacter() {
       return this.mappet$character;
    }

@Inject(
       method = {"method_6074"},
       at = {@At("HEAD")},
       cancellable = true
    )
    private void mappet$damage(class_1282 source, float amount, CallbackInfo ci) {
       class_1657 player = (class_1657)(Object)this;
       if (!player.method_37908().field_9236) {
          LegacyEvents.LivingDamageEvent event = new LegacyEvents.LivingDamageEvent(player, source, amount);
          CommonProxy.eventHandler.onEntityHurt(event);
          if (event.isCanceled()) {
             ci.cancel();
          }
       }
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
