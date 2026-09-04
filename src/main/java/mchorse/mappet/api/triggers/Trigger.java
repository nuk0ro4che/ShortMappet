package mchorse.mappet.api.triggers;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.api.triggers.blocks.AbstractTriggerBlock;
import mchorse.mappet.api.triggers.blocks.CommandTriggerBlock;
import mchorse.mappet.api.triggers.blocks.DialogueTriggerBlock;
import mchorse.mappet.api.triggers.blocks.EventTriggerBlock;
import mchorse.mappet.api.triggers.blocks.ScriptTriggerBlock;
import mchorse.mappet.api.triggers.blocks.SoundTriggerBlock;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class Trigger implements INBTSerializable<class_2487> {
   public final List<AbstractTriggerBlock> blocks = new ArrayList();
   private boolean empty;

   public Trigger() {
   }

   public Trigger(List<AbstractTriggerBlock> blocks) {
      this.blocks.addAll(blocks);
   }

   public void copy(Trigger trigger) {
      this.blocks.clear();

      for(AbstractTriggerBlock block : trigger.blocks) {
         String type = CommonProxy.getTriggerBlocks().getType(block);
         AbstractTriggerBlock newBlock = (AbstractTriggerBlock)CommonProxy.getTriggerBlocks().create(type);
         newBlock.deserializeNBT(block.serializeNBT());
         this.blocks.add(newBlock);
      }

      this.recalculateEmpty();
   }

   public void recalculateEmpty() {
      this.empty = true;

      for(AbstractTriggerBlock block : this.blocks) {
         if (!block.isEmpty()) {
            this.empty = false;
         }
      }

   }

   public void trigger(class_1309 target) {
      this.trigger(new DataContext(target));
   }

   public void trigger(class_1309 target, class_1297 entity) {
      this.trigger(new DataContext(target, entity));
   }

   public void trigger(DataContext context) {
      for(AbstractTriggerBlock block : this.blocks) {
         if (context.isCanceled()) {
            return;
         }

         block.triggerWithFrequency(context);
      }

   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 blocks = new class_2499();

      for(AbstractTriggerBlock block : this.blocks) {
         class_2487 blockTag = block.serializeNBT();
         blockTag.method_10582("Type", CommonProxy.getTriggerBlocks().getType(block));
         blocks.add(blockTag);
      }

      tag.method_10566("Blocks", blocks);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.blocks.clear();
      if (tag.method_10545("Sound")) {
         this.blocks.add(new SoundTriggerBlock(tag.method_10558("Sound")));
      }

      if (tag.method_10545("Trigger")) {
         this.blocks.add(new EventTriggerBlock(tag.method_10558("Trigger")));
      }

      if (tag.method_10545("Command")) {
         this.blocks.add(new CommandTriggerBlock(tag.method_10558("Command")));
      }

      if (tag.method_10545("Dialogue")) {
         this.blocks.add(new DialogueTriggerBlock(tag.method_10558("Dialogue")));
      }

      if (tag.method_10545("Script")) {
         this.blocks.add(new ScriptTriggerBlock(tag.method_10558("Script"), tag.method_10558("ScriptFunction")));
      }

      if (tag.method_10545("Blocks")) {
         class_2499 blocks = tag.method_10554("Blocks", 10);

         for(int i = 0; i < blocks.size(); ++i) {
            class_2487 blockTag = blocks.method_10602(i);
            AbstractTriggerBlock block = (AbstractTriggerBlock)CommonProxy.getTriggerBlocks().create(blockTag.method_10558("Type"));
            if (block != null) {
               block.deserializeNBT(blockTag);
               this.blocks.add(block);
            }
         }
      }

      this.recalculateEmpty();
   }

   public boolean isEmpty() {
      return this.empty;
   }
}
