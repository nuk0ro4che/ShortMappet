package mchorse.mappet.api.quests.chains;

import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.utils.nodes.Node;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2487;
import net.minecraft.class_2520;

public class QuestNode extends Node {
   public String quest = "";
   public String giver = "";
   public String receiver = "";
   public boolean autoAccept;
   public boolean allowRetake;
   public Checker condition = new Checker(true);

   @Environment(EnvType.CLIENT)
   protected String getDisplayTitle() {
      return this.quest;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = super.serializeNBT();
      if (!this.quest.isEmpty()) {
         tag.method_10582("Quest", this.quest);
      }

      if (!this.giver.isEmpty()) {
         tag.method_10582("Giver", this.giver);
      }

      if (!this.receiver.isEmpty()) {
         tag.method_10582("Receiver", this.receiver);
      }

      if (this.autoAccept) {
         tag.method_10556("AutoAccept", this.autoAccept);
      }

      if (this.allowRetake) {
         tag.method_10556("AllowRetake", this.allowRetake);
      }

      tag.method_10566("Condition", this.condition.serializeNBT());
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      if (tag.method_10545("Quest")) {
         this.quest = tag.method_10558("Quest");
      }

      if (tag.method_10545("Giver")) {
         this.giver = tag.method_10558("Giver");
      }

      if (tag.method_10545("Receiver")) {
         this.receiver = tag.method_10558("Receiver");
      }

      if (tag.method_10545("AutoAccept")) {
         this.autoAccept = tag.method_10577("AutoAccept");
      }

      if (tag.method_10545("AllowRetake")) {
         this.allowRetake = tag.method_10577("AllowRetake");
      }

      this.condition.deserializeNBT((class_2520)tag.method_10562("Condition"));
   }
}
