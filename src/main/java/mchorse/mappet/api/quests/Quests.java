package mchorse.mappet.api.quests;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.quests.PacketQuest;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_3222;

public class Quests implements INBTSerializable<class_2487> {
   public Map<String, Quest> quests = new LinkedHashMap();
   public boolean iterating;
   public List<Quest> toAdd = new ArrayList(2);

   public void initiate(class_1657 player) {
      for(Quest quest : this.quests.values()) {
         quest.initiate(player);
      }

   }

   public boolean add(Quest quest, class_1657 player) {
      if (this.has(quest.getId())) {
         return false;
      } else if (this.iterating) {
         this.toAdd.add(quest);
         return true;
      } else {
         this.quests.put(quest.getId(), quest);
         quest.initiate(player);
         quest.accept.trigger((class_1309)player);
         if (player instanceof class_3222) {
            Dispatcher.sendTo(new PacketQuest(quest.getId(), quest), (class_3222)player);
         }

         return true;
      }
   }

   public boolean complete(String id, class_1657 player) {
      return this.remove(id, player, true);
   }

   public boolean decline(String id, class_1657 player) {
      return this.remove(id, player, false);
   }

   public boolean remove(String id, class_1657 player, boolean reward) {
      Quest quest = (Quest)this.quests.remove(id);
      if (quest == null) {
         return false;
      } else {
         if (reward) {
            quest.reward(player);
         } else {
            quest.decline.trigger((class_1309)player);
         }

         if (player instanceof class_3222) {
            Dispatcher.sendTo(new PacketQuest(id, (Quest)null), (class_3222)player);
         }

         return true;
      }
   }

   public boolean has(String id) {
      return this.quests.containsKey(id);
   }

   public Quest getByName(String id) {
      return (Quest)this.quests.get(id);
   }

   public void copy(Quests quests) {
      this.quests.clear();

      for(Map.Entry<String, Quest> entry : quests.quests.entrySet()) {
         Quest quest = (Quest)Mappet.quests.load((String)entry.getKey());
         quest.partialDeserializeNBT(((Quest)entry.getValue()).partialSerializeNBT());
         this.quests.put((String)entry.getKey(), quest);
      }

   }

   public void flush(class_1657 player) {
      if (this.iterating) {
         this.iterating = false;

         for(Quest quest : this.toAdd) {
            this.add(quest, player);
         }

         this.toAdd.clear();
      }

   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();

      for(Map.Entry<String, Quest> entry : this.quests.entrySet()) {
         tag.method_10566((String)entry.getKey(), ((Quest)entry.getValue()).partialSerializeNBT());
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      for(String key : tag.method_10541()) {
         Quest quest = (Quest)Mappet.quests.load(key);
         if (quest != null) {
            quest.partialDeserializeNBT(tag.method_10562(key));
            this.quests.put(key, quest);
         }
      }

   }
}
