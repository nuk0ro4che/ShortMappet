package mchorse.mappet.mixins;

import net.minecraft.class_332;
import net.minecraft.class_3928;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_3928.class)
public abstract class ReceivingLevelScreenMixin extends class_437
{
   private ReceivingLevelScreenMixin()
   {
      super(null);
   }
   /**
    * Экран загрузки мира не вызывает Screen.render(), поэтому добавленные кнопки не рисуются
    */
   @Inject(method = {"method_25394"}, at = {@At("TAIL")})
   private void mappet$renderWidgets(class_332 context, int mouseX, int mouseY, float delta, CallbackInfo ci)
   {
      super.method_25394(context, mouseX, mouseY, delta);
   }
}