package mchorse.mappet.mixins;

import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.scripts.PacketClick;
import net.minecraft.class_1268;
import net.minecraft.class_239;
import net.minecraft.class_310;
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

   }

   @Inject(
      method = {"method_1583"},
      at = {@At("HEAD")}
   )
   private void mappet$rightClickEmpty(CallbackInfo ci) {
      if (this.field_1765 != null && this.field_1765.method_17783() == class_240.field_1333) {
         Dispatcher.sendToServer(new PacketClick(class_1268.field_5810));
      }

   }
}
