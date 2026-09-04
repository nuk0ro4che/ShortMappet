package mchorse.mappet.mixins;

import java.lang.reflect.Field;
import java.util.OptionalInt;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.compat.events.legacy.LegacyEvents;
import net.minecraft.class_1263;
import net.minecraft.class_1282;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_3222;
import net.minecraft.class_3908;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_3222.class})
public abstract class ServerPlayerContainerEventMixin {
   @Unique
   private class_1263 mappet$openInventory;

   @Inject(
      method = {"method_6078"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mappet$death(class_1282 source, CallbackInfo ci) {
      class_3222 player = (class_3222)(Object)this;
      if (!player.method_37908().field_9236) {
         LegacyEvents.LivingDeathEvent event = new LegacyEvents.LivingDeathEvent(player, source);
         CommonProxy.eventHandler.onMobKilled(event);
         if (event.isCanceled()) {
            ci.cancel();
         }
      }
   }

   @Inject(
      method = {"method_17355"},
      at = {@At("RETURN")}
   )
   private void mappet$openedContainer(class_3908 factory, CallbackInfoReturnable<OptionalInt> cir) {
      if (!((OptionalInt)cir.getReturnValue()).isEmpty()) {
         class_3222 player = (class_3222)(Object)this;
         class_1263 var10000;
         if (factory instanceof class_1263) {
            class_1263 value = (class_1263)factory;
            var10000 = value;
         } else {
            var10000 = mappet$findInventory(player.field_7512, player.method_31548());
         }

         class_1263 inventory = var10000;
         this.mappet$openInventory = inventory;
         CommonProxy.eventHandler.onPlayerOpenOrCloseContainer(new LegacyEvents.PlayerContainerEvent.Open(player, inventory));
      }
   }

   @Inject(
      method = {"method_7346"},
      at = {@At("HEAD")}
   )
   private void mappet$closedContainer(CallbackInfo ci) {
      class_3222 player = (class_3222)(Object)this;
      class_1263 inventory = this.mappet$openInventory != null ? this.mappet$openInventory : mappet$findInventory(player.field_7512, player.method_31548());
      CommonProxy.eventHandler.onPlayerOpenOrCloseContainer(new LegacyEvents.PlayerContainerEvent.Close(player, inventory));
      this.mappet$openInventory = null;
   }

   @Unique
   private static class_1263 mappet$findInventory(class_1703 handler, class_1661 fallback) {
      class_1263 playerInventory = null;

      for(Class<?> type = handler.getClass(); type != null; type = type.getSuperclass()) {
         for(Field field : type.getDeclaredFields()) {
            if (class_1263.class.isAssignableFrom(field.getType())) {
               try {
                  field.setAccessible(true);
                  Object value = field.get(handler);
                  if (value instanceof class_1661) {
                     class_1661 inventory = (class_1661)value;
                     playerInventory = inventory;
                  } else if (value instanceof class_1263) {
                     class_1263 inventory = (class_1263)value;
                     return inventory;
                  }
               } catch (RuntimeException | ReflectiveOperationException var11) {
               }
            }
         }
      }

      return (class_1263)(playerInventory == null ? fallback : playerInventory);
   }
}
