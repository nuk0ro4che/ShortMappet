package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.utils.EnumUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_1657;
import net.minecraft.class_2487;

public class QuestConditionBlock extends TargetConditionBlock {
   public QuestCheck quest;

   public QuestConditionBlock() {
      this.quest = QuestConditionBlock.QuestCheck.COMPLETED;
   }

   public boolean evaluateBlock(DataContext context) {
      if (this.target.mode == TargetMode.GLOBAL) {
         States states = this.target.getStates(context);
         if (this.quest != QuestConditionBlock.QuestCheck.ABSENT) {
            return this.quest == QuestConditionBlock.QuestCheck.PRESENT ? this.hasServerInProgress(context) : states.wasQuestCompleted(this.id);
         } else {
            return !states.wasQuestCompleted(this.id) && this.hasServerInProgress(context);
         }
      } else {
         ICharacter character = this.target.getCharacter(context);
         if (character != null) {
            if (this.quest != QuestConditionBlock.QuestCheck.ABSENT) {
               return this.quest == QuestConditionBlock.QuestCheck.PRESENT ? character.getQuests().has(this.id) : character.getStates().wasQuestCompleted(this.id);
            } else {
               return !character.getStates().wasQuestCompleted(this.id) && !character.getQuests().has(this.id);
            }
         } else {
            return false;
         }
      }
   }

   private boolean hasServerInProgress(DataContext context) {
      for(class_1657 player : context.server.method_3760().method_14571()) {
         if (Character.get(player).getQuests().has(this.id)) {
            return true;
         }
      }

      return false;
   }

   protected TargetMode getDefaultTarget() {
      return TargetMode.SUBJECT;
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      if (this.quest == QuestConditionBlock.QuestCheck.ABSENT) {
         return class_1074.method_4662("mappet.gui.conditions.quest.is_absent", new Object[]{this.id});
      } else {
         return this.quest == QuestConditionBlock.QuestCheck.PRESENT ? class_1074.method_4662("mappet.gui.conditions.quest.is_present", new Object[]{this.id}) : class_1074.method_4662("mappet.gui.conditions.quest.is_completed", new Object[]{this.id});
      }
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10569("Quest", this.quest.ordinal());
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.quest = (QuestCheck)EnumUtils.getValue(tag.method_10550("Quest"), QuestConditionBlock.QuestCheck.values(), QuestConditionBlock.QuestCheck.COMPLETED);
   }

   public static enum QuestCheck {
      ABSENT,
      PRESENT,
      COMPLETED;
      private static QuestCheck[] $values() {
         return new QuestCheck[]{ABSENT, PRESENT, COMPLETED};
      }
   }
}
