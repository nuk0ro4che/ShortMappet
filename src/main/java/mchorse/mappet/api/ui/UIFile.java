package mchorse.mappet.api.ui;

import net.minecraft.class_2487;


public class UIFile extends UI {
   public String script = "";
   public String function = "main";

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10582("Script", this.script == null ? "" : this.script);
      tag.method_10582("Function", this.function == null || this.function.isEmpty() ? "main" : this.function);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Script")) {
         this.script = tag.method_10558("Script");
      }

      if (tag.method_10545("Function")) {
         this.function = tag.method_10558("Function");
      }
   }
}
