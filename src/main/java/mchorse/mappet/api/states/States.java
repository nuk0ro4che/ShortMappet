package mchorse.mappet.api.states;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import mchorse.mappet.Mappet;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.mappet.events.StateChangedEvent;
import mchorse.mappet.utils.NBTToJsonLike;
import net.minecraft.class_2487;
import net.minecraft.class_2514;
import net.minecraft.class_2519;
import net.minecraft.class_2520;

public class States implements INBTSerializable<class_2487> {
   public static final String QUEST_PREFIX = "quests.";
   public static final String DIALOGUE_PREFIX = "dialogue.";
   public static final String FACTIONS_PREFIX = "factions.";
   public Map<String, Object> values = new HashMap();
   private File file;

   public States() {
   }

   public States(File file) {
      this.file = file;
   }

   protected void post(String id, Object previous, Object current) {
      Mappet.EVENT_BUS.post(new StateChangedEvent(this, id, previous, current));
   }

   public void add(String id, double value) {
      Object previous = this.values.get(id);
      if (previous == null || previous instanceof Number) {
         this.values.put(id, (previous == null ? (double)0.0F : ((Number)previous).doubleValue()) + value);
         this.post(id, previous, value);
      }

   }

   public void setNumber(String id, double value) {
      if (!Double.isNaN(value)) {
         Object previous = this.values.get(id);
         this.values.put(id, value);
         this.post(id, previous, value);
      }
   }

   public void setString(String id, String value) {
      Object previous = this.values.get(id);
      this.values.put(id, value);
      this.post(id, previous, value);
   }

   public double getNumber(String id) {
      Object object = this.values.get(id);
      return object instanceof Number ? ((Number)object).doubleValue() : (double)0.0F;
   }

   public boolean isNumber(String id) {
      Object object = this.values.get(id);
      return object instanceof Number;
   }

   public String getString(String id) {
      Object object = this.values.get(id);
      return object instanceof String ? (String)object : "";
   }

   public boolean isString(String id) {
      Object object = this.values.get(id);
      return object instanceof String;
   }

   public boolean reset(String id) {
      Object previous = this.values.remove(id);
      this.post(id, previous, (Object)null);
      return previous != null;
   }

   public boolean resetMasked(String id) {
      if (id.trim().equals("*")) {
         boolean wasEmpty = this.values.isEmpty();
         if (!wasEmpty) {
            this.clear();
         }

         return !wasEmpty;
      } else if (id.contains("*")) {
         id = id.replaceAll("\\*", ".*");
         Pattern pattern = Pattern.compile("^" + id + "$");
         int size = this.values.size();
         this.values.keySet().removeIf((key) -> pattern.matcher(key).matches());
         if (this.values.size() != size) {
            this.post((String)null, (Object)null, (Object)null);
            return true;
         } else {
            return false;
         }
      } else {
         return this.reset(id);
      }
   }

   public void clear() {
      this.values.clear();
      this.post((String)null, (Object)null, (Object)null);
   }

   public void copy(States states) {
      this.values.clear();
      this.values.putAll(states.values);
      this.post((String)null, (Object)null, (Object)null);
   }

   public void completeQuest(String id) {
      this.add("quests." + id, (double)1.0F);
   }

   public int getQuestCompletedTimes(String id) {
      return (int)this.getNumber("quests." + id);
   }

   public boolean wasQuestCompleted(String id) {
      return this.getQuestCompletedTimes(id) > 0;
   }

   public void addFactionScore(String id, int score, int defaultScore) {
      if (this.hasFaction(id)) {
         this.add("factions." + id, (double)score);
      } else {
         this.setNumber("factions." + id, (double)(defaultScore + score));
      }

   }

   public void setFactionScore(String id, int score) {
      this.setNumber("factions." + id, (double)score);
   }

   public int getFactionScore(String id) {
      return (int)this.getNumber("factions." + id);
   }

   public boolean clearFactionScore(String id) {
      return this.reset("factions." + id);
   }

   public void clearAllFactionScores() {
      this.values.keySet().removeIf((key) -> key.startsWith("factions."));
   }

   public boolean hasFaction(String id) {
      return this.values.containsKey("factions." + id);
   }

   public Set<String> getFactionNames() {
      Set<String> factionNames = new HashSet();

      for(String key : this.values.keySet()) {
         if (key.startsWith("factions.")) {
            factionNames.add(key.replace("factions.", ""));
         }
      }

      return factionNames;
   }

   public void readDialogue(String id, String marker) {
      this.add(this.getDialogueId(id, marker), (double)1.0F);
   }

   public boolean hasReadDialogue(String id, String marker) {
      return this.getReadDialogueTimes(id, marker) > 0;
   }

   public int getReadDialogueTimes(String id, String marker) {
      return (int)this.getNumber(this.getDialogueId(id, marker));
   }

   private String getDialogueId(String id, String marker) {
      id = "dialogue." + id;
      if (marker != null && !marker.isEmpty()) {
         id = id + ":" + marker;
      }

      return id;
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();

      for(Map.Entry<String, Object> entry : this.values.entrySet()) {
         if (entry.getValue() instanceof Number) {
            tag.method_10549((String)entry.getKey(), ((Number)entry.getValue()).doubleValue());
         } else if (entry.getValue() instanceof String) {
            tag.method_10582((String)entry.getKey(), (String)entry.getValue());
         }
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.values.clear();

      for(String key : tag.method_10541()) {
         class_2520 base = tag.method_10580(key);
         if (base.method_10711() == 8) {
            this.values.put(key, ((class_2519)base).method_10714());
         } else if (base instanceof class_2514) {
            this.values.put(key, ((class_2514)base).method_10697());
         }
      }

   }

   public void load() {
      if (this.file.exists()) {
         try {
            this.deserializeNBT(NBTToJsonLike.read(this.file));
         } catch (Exception e) {
            e.printStackTrace();
         }

      }
   }

   public boolean save() {
      try {
         NBTToJsonLike.write(this.file, this.serializeNBT());
         return true;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }
}
