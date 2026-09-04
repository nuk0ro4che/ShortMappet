package mchorse.mappet.api.dialogues;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.dialogues.nodes.CommentNode;
import mchorse.mappet.api.dialogues.nodes.CraftingNode;
import mchorse.mappet.api.dialogues.nodes.QuestChainNode;
import mchorse.mappet.api.dialogues.nodes.QuestDialogueNode;
import mchorse.mappet.api.dialogues.nodes.ReactionNode;
import mchorse.mappet.api.dialogues.nodes.ReplyNode;
import mchorse.mappet.api.events.EventContext;
import mchorse.mappet.api.utils.DataContext;

public class DialogueContext extends EventContext {
   public ReactionNode reactionNode;
   public CommentNode commentNode;
   public List<ReplyNode> replyNodes = new ArrayList();
   public CraftingNode crafting;
   public QuestChainNode questChain;
   public QuestDialogueNode quest;

   public DialogueContext(DataContext data) {
      super(data);
   }

   public void resetAll() {
      this.reactionNode = null;
      this.commentNode = null;
      this.replyNodes.clear();
      this.crafting = null;
      this.questChain = null;
      this.quest = null;
   }

   public void addReply(ReplyNode node) {
      this.replyNodes.add(node);
      this.crafting = null;
      this.questChain = null;
      this.quest = null;
   }

   public void setCrafting(CraftingNode node) {
      this.replyNodes.clear();
      this.crafting = node;
      this.questChain = null;
      this.quest = null;
   }

   public void setQuestChain(QuestChainNode node) {
      this.replyNodes.clear();
      this.crafting = null;
      this.questChain = node;
      this.quest = null;
   }

   public void setQuest(QuestDialogueNode node) {
      this.replyNodes.clear();
      this.crafting = null;
      this.questChain = null;
      this.quest = node;
   }
}
