package mchorse.mappet.mixins;

import java.util.HashSet;
import java.util.Set;
import mchorse.mappet.compat.BlockEntityData;
import net.minecraft.class_2487;
import net.minecraft.class_2586;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_2586.class})
public abstract class BlockEntityMixin {
   @Inject(
           method = {"method_11007"},
           at = {@At("RETURN")}
   )
   private void mappet$writeData(class_2487 nbt, CallbackInfo ci) {
      class_2487 data = BlockEntityData.get((class_2586)(Object)this);
      if (!data.method_33133()) {
         nbt.method_10566("MappetData", data.method_10553());
      }
   }

   @Inject(
           method = {"method_11014"},
           at = {@At("RETURN")}
   )
   private void mappet$readData(class_2487 nbt, CallbackInfo ci) {
      class_2487 data = BlockEntityData.get((class_2586)(Object)this);

      Set<String> keys = new HashSet<>();
      for (Object keyObj : data.method_10541()) {
         if (keyObj instanceof String) {
            keys.add((String) keyObj);
         }
      }

      for (String key : keys) {
         data.method_10551(key);
      }

      if (nbt.method_10545("MappetData")) {
         data.method_10543(nbt.method_10562("MappetData"));
      }
   }
}