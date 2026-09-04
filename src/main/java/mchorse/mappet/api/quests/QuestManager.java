package mchorse.mappet.api.quests;

import java.io.File;
import mchorse.mappet.api.utils.manager.BaseManager;
import net.minecraft.class_2487;

public class QuestManager extends BaseManager<Quest> {
   public QuestManager(File folder) {
      super(folder);
   }

   protected Quest createData(String id, class_2487 tag) {
      Quest quest = new Quest();
      if (tag != null) {
         quest.deserializeNBT(tag);
      }

      return quest;
   }
}
