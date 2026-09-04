package mchorse.mappet.api.dialogues.nodes;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.events.EventContext;
import mchorse.mappet.api.events.nodes.EventBaseNode;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_2487;

public class QuestDialogueNode extends EventBaseNode {
   public String quest = "";
   public boolean skipIfCompleted;

   public int execute(EventContext context) {
      if (context instanceof DialogueContext) {
         class_1657 player = context.data.getPlayer();
         if (this.skipIfCompleted && this.isPlayerCompletedQuest(player)) {
            return 0;
         }

         ((DialogueContext)context).setQuest(this);
      }

      return -1;
   }

   private boolean isPlayerCompletedQuest(class_1657 player) {
      Quest quest = (Quest)Mappet.quests.load(this.quest);
      if (quest != null) {
         ICharacter character = Character.get(player);
         if (character != null) {
            return character.getStates().wasQuestCompleted(this.quest);
         }
      }

      return false;
   }

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return this.quest;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      tag.method_10582("Quest", this.quest);
      tag.method_10556("SkipIfCompleted", this.skipIfCompleted);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Quest")) {
         this.quest = tag.method_10558("Quest");
      }

      this.skipIfCompleted = tag.method_10577("SkipIfCompleted");
   }
}
