package mchorse.mappet.api.triggers.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;

public abstract class StringTriggerBlock extends AbstractTriggerBlock {
   public String string = "";

   public StringTriggerBlock() {
   }

   public StringTriggerBlock(String string) {
      this.string = string;
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      return this.string.isEmpty() ? super.stringify() : this.string;
   }

   public boolean isEmpty() {
      return this.string.isEmpty();
   }

   protected void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582(this.getKey(), this.string);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.string = tag.method_10558(this.getKey());
   }

   protected abstract String getKey();
}
