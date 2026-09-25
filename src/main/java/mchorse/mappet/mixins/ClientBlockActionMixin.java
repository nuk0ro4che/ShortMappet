package mchorse.mappet.mixins;

import mchorse.mappet.client.ClientBlockInteractHandler;
import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_636;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_636.class})
public class ClientBlockActionMixin {
	@Inject(
		method = {"method_2899"},
		at = {@At("HEAD")},
		cancellable = true
	)
	private void mappet$clientBreakBlock(class_2338 pos, CallbackInfoReturnable<Boolean> cir) {
		class_310 mc = class_310.method_1551();
		if (mc == null || mc.field_1687 == null || mc.field_1724 == null) {
			return;
		}

		DataContext context = ClientBlockInteractHandler.onBreakBlock(pos, mc.field_1687.method_8320(pos));
		if (context != null && context.isCanceled()) {
			cir.setReturnValue(false);
		}
	}
}