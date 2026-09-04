package mchorse.mappet.api.quests.chains;

import java.io.File;
import mchorse.mappet.CommonProxy;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.manager.BaseManager;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import net.minecraft.class_1657;
import net.minecraft.class_2487;

public class QuestChainManager extends BaseManager<QuestChain> {
   public QuestChainManager(File folder) {
      super(folder);
   }

   protected QuestChain createData(String id, class_2487 tag) {
      QuestChain chain = new QuestChain(CommonProxy.getChains());
      if (tag != null) {
         chain.deserializeNBT(tag);
      }

      return chain;
   }

   public QuestContext evaluate(String id, class_1657 player, String subject) {
      ICharacter character = Character.get(player);
      QuestContext context = new QuestContext(player, subject);
      QuestChain chain = (QuestChain)this.load(id);
      if (character != null && chain != null) {
         context.data = new DataContext(player);

         for(QuestNode node : chain.getRoots()) {
            int size = context.quests.size();
            this.evaluateRecursive(context, character, chain, node);
            if (node.allowRetake && !context.canceled && context.nesting > 0 && context.nesting == context.completed && size == context.quests.size() && node.condition.check(context.data)) {
               Quest quest = (Quest)Mappet.quests.load(node.quest);
               if (quest != null) {
                  QuestInfo info = this.giveNewQuest(context, character, node, quest);
                  if (info != null) {
                     context.quests.add(info);
                  }
               }
            }

            context.nesting = 0;
            context.completed = 0;
            context.lastTimesCompleted = 0;
         }

         return context;
      } else {
         return context;
      }
   }

   private void evaluateRecursive(QuestContext context, ICharacter character, QuestChain chain, QuestNode node) {
      int timesCompleted = character.getStates().getQuestCompletedTimes(node.quest);
      boolean wasCompleted = timesCompleted > 0;
      Quest quest = character.getQuests().getByName(node.quest);
      QuestInfo info = null;
      if (quest != null) {
         if (quest.isComplete(context.player)) {
            if (!context.subject.equals(node.receiver)) {
               context.canceled = true;
               return;
            }

            info = new QuestInfo(quest, QuestStatus.COMPLETED);
         } else if (context.subject.equals(node.giver)) {
            info = new QuestInfo(quest, QuestStatus.UNAVAILABLE);
         }
      }

      if (info == null) {
         quest = (Quest)Mappet.quests.load(node.quest);
         if (quest != null && this.canTakeQuest(context, node, timesCompleted)) {
            info = this.giveNewQuest(context, character, node, quest);
         }
      }

      if (info != null) {
         context.quests.add(info);
         context.canceled = true;
      } else {
         int nesting = context.nesting;
         int completed = context.completed;
         context.canceled = false;

         for(QuestNode child : chain.getChildren(node)) {
            context.nesting = nesting + 1;
            context.completed = completed + (wasCompleted ? 1 : 0);
            context.lastTimesCompleted = timesCompleted;
            this.evaluateRecursive(context, character, chain, child);
         }

      }
   }

   private boolean canTakeQuest(QuestContext context, QuestNode node, int timesCompleted) {
      if (!context.subject.equals(node.giver)) {
         return false;
      } else if (!node.condition.check(context.data)) {
         return false;
      } else if (node.allowRetake) {
         if (context.nesting == 0) {
            return timesCompleted == 0;
         } else {
            return timesCompleted < context.lastTimesCompleted;
         }
      } else {
         return timesCompleted == 0 && context.nesting == context.completed;
      }
   }

   private QuestInfo giveNewQuest(QuestContext context, ICharacter character, QuestNode node, Quest quest) {
      if (node.autoAccept) {
         character.getQuests().add(quest, context.player);
         if (context.subject.equals(node.giver)) {
            return new QuestInfo(quest, QuestStatus.UNAVAILABLE);
         } else {
            context.canceled = true;
            return null;
         }
      } else {
         return new QuestInfo(quest, QuestStatus.AVAILABLE);
      }
   }
}
