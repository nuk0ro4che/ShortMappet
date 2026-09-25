package mchorse.mappet.mixins;

import mchorse.mappet.client.ClientVisualBlocks;
import mchorse.mappet.client.ClientVisualBlocks.Entry;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_638.class})
public class ClientWorldOverrideMixin {
	@Inject(
		method = {"method_30092"},
		at = {@At("HEAD")},
		cancellable = true
	)
	private void mappet$swapBlockState(class_2338 pos, class_2680 state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
		Entry entry = ClientVisualBlocks.get(pos);
		if (entry == null) {
			return;
		}

		if (state == entry.override || state.method_26204() == entry.override.method_26204()) {
			return;
		}

		class_2248 incoming = state.method_26204();

		if (incoming == class_2246.field_10124) {
			ClientVisualBlocks.remove(pos);
			return;
		}

		if (incoming == entry.real) {
			cir.setReturnValue(((class_1937) (Object) this).method_8652(pos, entry.override, flags));
		} else {
			ClientVisualBlocks.remove(pos);
		}
	}
}