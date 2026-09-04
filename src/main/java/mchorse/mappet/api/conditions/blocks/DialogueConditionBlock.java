package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.capabilities.character.ICharacter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_2487;

public class DialogueConditionBlock extends TargetConditionBlock {
   public String marker = "";

   public boolean evaluateBlock(DataContext context) {
      if (this.target.mode == TargetMode.GLOBAL) {
         return false;
      } else {
         ICharacter character = this.target.getCharacter(context);
         return character != null && character.getStates().hasReadDialogue(this.id, this.marker);
      }
   }

   protected TargetMode getDefaultTarget() {
      return TargetMode.SUBJECT;
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      return class_1074.method_4662("mappet.gui.conditions.dialogue.was_read", new Object[]{this.id});
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10582("Marker", this.marker);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.marker = tag.method_10558("Marker");
   }
}
