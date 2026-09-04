package mchorse.mappet.network.server.quests;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.dialogues.nodes.ReactionNode;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.quests.chains.QuestStatus;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.common.quests.PacketQuestAction;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerQuestAction extends ServerMessageHandler<PacketQuestAction> {
   public void run(class_3222 player, PacketQuestAction message) {
      ICharacter character = Character.get(player);
      if (message.status == QuestStatus.AVAILABLE) {
         Quest quest = (Quest)Mappet.quests.load(message.id);
         if (quest != null) {
            character.getQuests().add(quest, player);
         }
      } else if (message.status == QuestStatus.COMPLETED) {
         Quest quest = character.getQuests().getByName(message.id);
         if (quest != null && quest.isComplete(player)) {
            character.getQuests().complete(message.id, player);
            DialogueContext context = character.getDialogueContext();
            if (context.questChain != null) {
               Mappet.dialogues.handleContext(player, character.getDialogue(), context, (ReactionNode)null);
            }
         }
      } else if (message.status == QuestStatus.CANCELED) {
         Quest quest = character.getQuests().getByName(message.id);
         if (quest != null && quest.cancelable) {
            character.getQuests().remove(message.id, player, false);
         }
      }

   }
}
