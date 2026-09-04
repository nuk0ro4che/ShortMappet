package mchorse.mappet.api.quests.chains;

import java.util.ArrayList;
import java.util.List;
import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_1657;

public class QuestContext {
   public class_1657 player;
   public String subject;
   public List<QuestInfo> quests = new ArrayList();
   public int nesting;
   public int completed;
   public int lastTimesCompleted;
   public boolean canceled;
   public DataContext data;

   public QuestContext(class_1657 player, String subject) {
      this.player = player;
      this.subject = subject;
   }
}
