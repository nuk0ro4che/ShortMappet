package mchorse.mappet.mixins;

import mchorse.mappet.api.scripts.code.items.ScriptItemStack;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.client.ClientTriggers;
import mchorse.mappet.utils.ItemUtils;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1309.class})
public abstract class ClientPlayerUseItemMixin {
   private class_1309 mappet$self() {
      return (class_1309)(Object)this;
   }

   @Inject(
      method = {"method_6076"},
      at = {@At("HEAD")}
   )
   private void mappet$useTick(CallbackInfo ci) {
      class_1309 self = this.mappet$self();

      if (!(self instanceof class_746) || !self.method_37908().field_9236) {
         return;
      }

      class_1799 stack = self.method_6030();

      if (stack.method_7960()) {
         return;
      }

      DataContext context = DataContext.client(self);
      context.getValues().put("item", ScriptItemStack.create(stack));

      if (ItemUtils.isDrinkable(stack)) {
         ClientTriggers.trigger("player_drink_tick", context);
      } else if (ItemUtils.isFood(stack)) {
         ClientTriggers.trigger("player_eat_tick", context);
      }
   }

   @Inject(
      method = {"method_6040"},
      at = {@At("HEAD")}
   )
   private void mappet$useFinish(CallbackInfo ci) {
      class_1309 self = this.mappet$self();

      if (!(self instanceof class_746) || !self.method_37908().field_9236) {
         return;
      }

      class_1799 stack = self.method_6030();

      if (stack.method_7960()) {
         return;
      }

      DataContext context = DataContext.client(self);
      context.getValues().put("item", ScriptItemStack.create(stack));

      if (ItemUtils.isDrinkable(stack)) {
         ClientTriggers.trigger("player_drink_finish", context);
      } else if (ItemUtils.isFood(stack)) {
         ClientTriggers.trigger("player_eat_finish", context);
      }
   }
}
