package mchorse.mappet.api.events.nodes;

import mchorse.mappet.api.events.EventContext;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.triggers.blocks.DialogueTriggerBlock;
import mchorse.mappet.api.triggers.blocks.EventTriggerBlock;
import mchorse.mappet.api.triggers.blocks.ScriptTriggerBlock;
import mchorse.mappet.api.triggers.blocks.StringTriggerBlock;
import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_2487;

public class TriggerNode extends EventBaseNode {
   public Trigger trigger = new Trigger();
   public String customData = "";
   public boolean cancel;

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return class_1074.method_4662("mappet.gui.trigger.quantity", new Object[]{this.trigger.blocks.size()});
   }

   public int execute(EventContext context) {
      DataContext newContext = this.apply(context);
      this.trigger.trigger(newContext);
      if (this.cancel) {
         if (!context.data.isCanceled()) {
            context.data.cancel(newContext.isCanceled());
         }

         return this.booleanToExecutionCode(true);
      } else {
         return this.booleanToExecutionCode(!newContext.isCanceled());
      }
   }

   public DataContext apply(EventContext event) {
      DataContext context = event.data.copy();
      context.parse(context.process(this.customData));
      return context;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10566("Trigger", this.trigger.serializeNBT());
      tag.method_10582("CustomData", this.customData);
      tag.method_10556("Cancel", this.cancel);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.trigger.deserializeNBT(tag.method_10562("Trigger"));
      this.customData = tag.method_10558("CustomData");
      this.cancel = tag.method_10577("Cancel");
      String type = tag.method_10558("Type");
      StringTriggerBlock block = null;
      if (type.equals("event")) {
         block = new EventTriggerBlock();
         block.string = tag.method_10558("DataId");
      } else if (type.equals("dialogue")) {
         block = new DialogueTriggerBlock();
         block.string = tag.method_10558("DataId");
      } else if (type.equals("script")) {
         block = new ScriptTriggerBlock();
         block.string = tag.method_10558("DataId");
         ((ScriptTriggerBlock)block).function = tag.method_10558("Function");
      }

      if (block != null) {
         this.trigger.blocks.add(block);
      }

   }
}
