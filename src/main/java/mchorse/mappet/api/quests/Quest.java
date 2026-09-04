package mchorse.mappet.api.quests;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.quests.objectives.AbstractObjective;
import mchorse.mappet.api.quests.objectives.KillObjective;
import mchorse.mappet.api.quests.objectives.StateObjective;
import mchorse.mappet.api.quests.rewards.IReward;
import mchorse.mappet.api.triggers.Trigger;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mclib.utils.TextUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class Quest extends AbstractData implements INBTPartialSerializable {
   public String title = "";
   public String story = "";
   public boolean cancelable = true;
   public boolean visible = true;
   public boolean instant;
   public Trigger accept = new Trigger();
   public Trigger decline = new Trigger();
   public Trigger complete = new Trigger();
   public final List<AbstractObjective> objectives = new ArrayList();
   public final List<IReward> rewards = new ArrayList();
   private boolean initated;

   public void initiate(class_1657 player) {
      if (!this.initated) {
         for(AbstractObjective objective : this.objectives) {
            objective.initiate(player);
         }

         this.initated = true;
      }
   }

   public String getProcessedTitle() {
      return TextUtils.processColoredText(this.title);
   }

   public Quest setStory(String title, String story) {
      this.title = title;
      this.story = story;
      return this;
   }

   public Quest addObjective(AbstractObjective objective) {
      this.objectives.add(objective);
      return this;
   }

   public Quest addReward(IReward reward) {
      this.rewards.add(reward);
      return this;
   }

   public void mobWasKilled(class_1657 player, class_1297 entity) {
      for(AbstractObjective objective : this.objectives) {
         if (objective instanceof KillObjective) {
            ((KillObjective)objective).playerKilled(player, entity);
         }
      }

   }

   public boolean stateWasUpdated(class_1657 player) {
      int i = 0;

      for(AbstractObjective objective : this.objectives) {
         if (objective instanceof StateObjective) {
            i += ((StateObjective)objective).updateValue(player) ? 1 : 0;
         }
      }

      return i > 0;
   }

   public boolean isComplete(class_1657 player) {
      boolean result = true;

      for(AbstractObjective objective : this.objectives) {
         result = result && objective.isComplete(player);
      }

      return result;
   }

   public void reward(class_1657 player) {
      for(AbstractObjective objective : this.objectives) {
         objective.complete(player);
      }

      for(IReward reward : this.rewards) {
         reward.reward(player);
      }

      this.complete.trigger((class_1309)player);
      ICharacter character = Character.get(player);
      if (character != null) {
         character.getStates().completeQuest(this.getId());
      }

      Mappet.states.completeQuest(this.getId());
   }

   public boolean rewardIfComplete(class_1657 player) {
      if (!this.isComplete(player)) {
         return false;
      } else {
         this.reward(player);
         return true;
      }
   }

   public class_2487 partialSerializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 objectives = new class_2499();
      tag.method_10566("Objectives", objectives);

      for(AbstractObjective objective : this.objectives) {
         objectives.add(objective.partialSerializeNBT());
      }

      tag.method_10556("Visible", this.visible);
      return tag;
   }

   public void partialDeserializeNBT(class_2487 tag) {
      if (tag.method_10573("Objectives", 9)) {
         class_2499 list = tag.method_10554("Objectives", 10);

         for(int i = 0; i < Math.min(list.size(), this.objectives.size()); ++i) {
            ((AbstractObjective)this.objectives.get(i)).partialDeserializeNBT(list.method_10602(i));
         }
      }

      if (tag.method_10545("Visible")) {
         this.visible = tag.method_10577("Visible");
      }

   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 objectives = new class_2499();
      class_2499 rewards = new class_2499();
      tag.method_10582("Title", this.title);
      tag.method_10582("Story", this.story);
      tag.method_10556("Cancelable", this.cancelable);
      tag.method_10556("Instant", this.instant);
      tag.method_10556("Visible", this.visible);
      class_2487 accept = this.accept.serializeNBT();
      class_2487 decline = this.decline.serializeNBT();
      class_2487 complete = this.complete.serializeNBT();
      if (accept.method_10546() > 0) {
         tag.method_10566("Accept", accept);
      }

      if (decline.method_10546() > 0) {
         tag.method_10566("Decline", decline);
      }

      if (complete.method_10546() > 0) {
         tag.method_10566("Complete", complete);
      }

      tag.method_10566("Objectives", objectives);
      tag.method_10566("Rewards", rewards);

      for(AbstractObjective objective : this.objectives) {
         class_2487 item = objective.serializeNBT();
         item.method_10582("Type", objective.getType());
         objectives.add(item);
      }

      for(IReward reward : this.rewards) {
         class_2487 item = (class_2487)reward.serializeNBT();
         item.method_10582("Type", reward.getType());
         rewards.add(item);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.title = tag.method_10558("Title");
      this.story = tag.method_10558("Story");
      if (tag.method_10545("Cancelable")) {
         this.cancelable = tag.method_10577("Cancelable");
      }

      if (tag.method_10545("Instant")) {
         this.instant = tag.method_10577("Instant");
      }

      if (tag.method_10545("Accept")) {
         this.accept.deserializeNBT(tag.method_10562("Accept"));
      }

      if (tag.method_10545("Decline")) {
         this.decline.deserializeNBT(tag.method_10562("Decline"));
      }

      if (tag.method_10545("Complete")) {
         this.complete.deserializeNBT(tag.method_10562("Complete"));
      }

      if (tag.method_10545("Visible")) {
         this.visible = tag.method_10577("Visible");
      }

      if (tag.method_10545("Objectives")) {
         class_2499 list = tag.method_10554("Objectives", 10);

         for(int i = 0; i < list.size(); ++i) {
            class_2487 tagCompound = list.method_10602(i);
            AbstractObjective objective = AbstractObjective.fromType(tagCompound.method_10558("Type"));
            if (objective != null) {
               objective.deserializeNBT(tagCompound);
               this.objectives.add(objective);
            }
         }
      }

      if (tag.method_10545("Rewards")) {
         class_2499 list = tag.method_10554("Rewards", 10);

         for(int i = 0; i < list.size(); ++i) {
            class_2487 tagCompound = list.method_10602(i);
            IReward reward = IReward.fromType(tagCompound.method_10558("Type"));
            if (reward != null) {
               reward.deserializeNBT(tagCompound);
               this.rewards.add(reward);
            }
         }
      }

   }
}
