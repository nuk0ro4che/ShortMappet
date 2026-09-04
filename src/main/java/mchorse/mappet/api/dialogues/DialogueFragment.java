package mchorse.mappet.api.dialogues;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mclib.utils.TextUtils;
import net.minecraft.class_2487;

public class DialogueFragment implements INBTSerializable<class_2487> {
   public String text = "";
   public int color = 16777215;

   public static String process(String text) {
      return TextUtils.processColoredText(text.replace("\\n", "\n"));
   }

   public String getProcessedText() {
      return process(this.text);
   }

   public DialogueFragment copy() {
      DialogueFragment fragment = new DialogueFragment();
      fragment.text = this.text;
      fragment.color = this.color;
      return fragment;
   }

   public DialogueFragment process(DataContext context) {
      this.text = context.process(this.text);
      return this;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10582("Text", this.text);
      tag.method_10569("Color", this.color);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Text")) {
         this.text = tag.method_10558("Text");
      }

      if (tag.method_10545("Color")) {
         this.color = tag.method_10550("Color");
      }

   }
}
