package mchorse.mappet.api.scripts.code.mappet;

import java.util.Set;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.quests.Quest;
import mchorse.mappet.api.quests.Quests;
import mchorse.mappet.api.scripts.user.mappet.IMappetQuests;
import net.minecraft.class_1657;

public class MappetQuests implements IMappetQuests {
   public Quests quests;
   public class_1657 player;

   public MappetQuests(Quests quests, class_1657 player) {
      this.quests = quests;
      this.player = player;
   }

   public boolean has(String id) {
      return this.quests.has(id);
   }

   public boolean add(String id) {
      if (this.quests.has(id)) {
         return false;
      } else {
         Quest quest = (Quest)Mappet.quests.load(id);
         if (quest != null) {
            this.quests.add(quest, this.player);
         }

         return quest != null;
      }
   }

   public boolean isComplete(String id) {
      Quest quest = this.quests.getByName(id);
      return quest != null && quest.isComplete(this.player);
   }

   public boolean complete(String id) {
      return this.quests.complete(id, this.player);
   }

   public boolean decline(String id) {
      return this.quests.decline(id, this.player);
   }

   public Set<String> getIds() {
      return this.quests.quests.keySet();
   }
}
