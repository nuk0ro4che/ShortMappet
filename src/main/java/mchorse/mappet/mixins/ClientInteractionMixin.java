package mchorse.mappet.mixins;

import mchorse.mappet.client.ClientTriggers;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketClick;
import net.minecraft.class_1268;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_310.class})
public abstract class ClientInteractionMixin {
   @Shadow
   public class_239 field_1765;

   @Inject(
      method = {"method_1536"},
      at = {@At("HEAD")}
   )
   private void mappet$leftClickEmpty(CallbackInfoReturnable<Boolean> cir) {
      if (this.field_1765 != null && this.field_1765.method_17783() == class_240.field_1333) {
         Dispatcher.sendToServer(new PacketClick(class_1268.field_5808));
      }

      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         ClientTriggers.trigger("player_lmb", DataContext.client(mc.field_1724));
      }

   }

   @Inject(
      method = {"method_1583"},
      at = {@At("HEAD")}
   )
   private void mappet$rightClickEmpty(CallbackInfo ci) {
      if (this.field_1765 != null && this.field_1765.method_17783() == class_240.field_1333) {
         Dispatcher.sendToServer(new PacketClick(class_1268.field_5810));
      }

      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         DataContext context = DataContext.client(mc.field_1724);
         ClientTriggers.trigger("player_rmb", context);

         if (!mc.field_1724.method_6047().method_7960()) {
            ClientTriggers.trigger("player_item_interact", context);
         }
      }

   }
}
