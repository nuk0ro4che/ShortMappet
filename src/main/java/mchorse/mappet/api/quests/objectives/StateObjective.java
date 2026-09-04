package mchorse.mappet.api.quests.objectives;

import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_1657;
import net.minecraft.class_2487;

public class StateObjective extends AbstractObjective {
   public Checker expression = new Checker();
   private boolean result;
   private String compiledMessage;

   public void initiate(class_1657 player) {
      super.initiate(player);
      if (this.message.contains("${")) {
         this.compiledMessage = (new DataContext(player)).process(this.message);
      }

   }

   public boolean isComplete(class_1657 player) {
      return this.result;
   }

   public boolean updateValue(class_1657 player) {
      boolean result = this.result;
      DataContext data = new DataContext(player);
      this.result = this.expression.check(data);
      if (this.message.contains("${")) {
         this.compiledMessage = data.process(this.message);
         return true;
      } else {
         return this.result != result;
      }
   }

   public void complete(class_1657 player) {
   }

   public String stringifyObjective(class_1657 player) {
      return this.compiledMessage == null ? this.message : this.compiledMessage;
   }

   public String getType() {
      return "state";
   }

   public class_2487 partialSerializeNBT() {
      class_2487 tag = new class_2487();
      if (this.result) {
         tag.method_10556("Result", this.result);
      }

      return tag;
   }

   public void partialDeserializeNBT(class_2487 tag) {
      if (tag.method_10545("Result")) {
         this.result = tag.method_10577("Result");
      }

   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10566("Expression", this.expression.serializeNBT());
      tag.method_10556("Result", this.result);
      if (this.compiledMessage != null) {
         tag.method_10582("CompiledMessage", this.compiledMessage);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.expression.deserializeNBT(tag.method_10580("Expression"));
      this.result = tag.method_10577("Result");
      if (tag.method_10545("CompiledMessage")) {
         this.compiledMessage = tag.method_10558("CompiledMessage");
      } else {
         this.compiledMessage = null;
      }

   }
}
