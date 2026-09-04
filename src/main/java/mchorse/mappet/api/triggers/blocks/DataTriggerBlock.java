package mchorse.mappet.api.triggers.blocks;

import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_2487;

public abstract class DataTriggerBlock extends StringTriggerBlock {
   public String customData = "";

   public DataTriggerBlock() {
   }

   public DataTriggerBlock(String string) {
      super(string);
   }

   protected DataContext apply(DataContext context) {
      if (!this.customData.isEmpty()) {
         context = context.copy();
         context.parse(this.customData);
      }

      return context;
   }

   protected void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("CustomData", this.customData);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.customData = tag.method_10558("CustomData");
   }
}
