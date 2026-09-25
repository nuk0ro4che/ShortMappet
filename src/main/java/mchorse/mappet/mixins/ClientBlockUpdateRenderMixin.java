package mchorse.mappet.mixins;

import mchorse.mappet.Mappet;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2626;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_634;
import net.minecraft.class_7923;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public abstract class ClientBlockUpdateRenderMixin {
   @Inject(
      method = {"method_11136"},
      at = {@At("TAIL")}
   )
   private void mappet$forceSectionRender(class_2626 packet, CallbackInfo ci) {
      class_2680 state = packet.method_11308();
      if (state == null || state.method_26204() == class_2246.field_10124) {
         return;
      }

      class_310 client = class_310.method_1551();
      if (client == null || client.field_1687 == null || client.field_1769 == null) {
         return;
      }

      class_2338 pos = packet.method_11309();
      if (pos != null) {
         class_2680 current = client.field_1687.method_8320(pos);
         Mappet.logInfo("CLIENT-BLOCKUPDATE at " + pos.method_10263() + "," + pos.method_10264() + "," + pos.method_10260() + " incoming=" + class_7923.field_41175.method_10221(state.method_26204()) + " current=" + class_7923.field_41175.method_10221(current.method_26204()) + " applied=" + (current.method_26204() == state.method_26204()));
         client.field_1769.method_18145(pos.method_10263(), pos.method_10264(), pos.method_10260());
      }
   }
}