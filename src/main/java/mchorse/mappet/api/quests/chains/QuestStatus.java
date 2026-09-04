package mchorse.mappet.api.quests.chains;

import net.minecraft.class_124;

public enum QuestStatus {
   AVAILABLE(class_124.field_1068),
   UNAVAILABLE(class_124.field_1080),
   COMPLETED(class_124.field_1065),
   CANCELED(class_124.field_1055);

   public final class_124 formatting;

   private QuestStatus(class_124 formatting) {
      this.formatting = formatting;
   }
   private static QuestStatus[] $values() {
      return new QuestStatus[]{AVAILABLE, UNAVAILABLE, COMPLETED, CANCELED};
   }
}
