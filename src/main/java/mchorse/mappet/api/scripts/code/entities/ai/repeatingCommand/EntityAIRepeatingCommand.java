package mchorse.mappet.api.scripts.code.entities.ai.repeatingCommand;

import net.minecraft.class_1297;
import net.minecraft.class_1352;

public class EntityAIRepeatingCommand extends class_1352 {
   private class_1297 entity;
   private String command;
   private int executionInterval;
   private int tickCounter;

   public EntityAIRepeatingCommand(class_1297 entity, String command, int executionInterval) {
      this.entity = entity;
      this.command = command;
      this.executionInterval = executionInterval;
      this.tickCounter = 0;
   }

   public boolean method_6264() {
      return true;
   }

   public void method_6268() {
      ++this.tickCounter;
      if (this.tickCounter >= this.executionInterval) {
         this.entity.method_5682().method_3734().method_44252(this.entity.method_5671(), this.command);
         this.tickCounter = 0;
      }

   }

   public String getCommand() {
      return this.command;
   }
}
