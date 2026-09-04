package mchorse.mappet.api.factions;

import java.util.HashMap;
import java.util.Map;
import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.AbstractData;
import mchorse.mappet.api.utils.DataContext;
import net.minecraft.class_1657;
import net.minecraft.class_2487;

public class Faction extends AbstractData {
   public String title = "";
   public Checker visible = new Checker(true);
   public int color = 16777215;
   public int score = 500;
   public FactionAttitude playerAttitude;
   public FactionAttitude othersAttitude;
   public FactionRelation ownRelation;
   public Map<String, FactionAttitude> relations;

   public Faction() {
      this.playerAttitude = FactionAttitude.PASSIVE;
      this.othersAttitude = FactionAttitude.PASSIVE;
      this.ownRelation = new FactionRelation();
      this.relations = new HashMap();
   }

   public FactionAttitude get(States states) {
      if (states.hasFaction(this.getId())) {
         return this.ownRelation.getAttitude(states.getFactionScore(this.getId()));
      } else {
         for(String key : this.relations.keySet()) {
            if (states.hasFaction(key)) {
               return (FactionAttitude)this.relations.get(key);
            }
         }

         return this.playerAttitude;
      }
   }

   public FactionAttitude get(String faction) {
      if (faction.equals(this.getId())) {
         return FactionAttitude.FRIENDLY;
      } else {
         FactionAttitude attitude = (FactionAttitude)this.relations.get(faction);
         return attitude == null ? this.othersAttitude : attitude;
      }
   }

   public boolean isVisible(class_1657 player) {
      return this.visible.check(new DataContext(player));
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10582("Title", this.title);
      tag.method_10566("Visible", this.visible.serializeNBT());
      tag.method_10569("Color", this.color);
      tag.method_10569("DefaultScore", this.score);
      tag.method_10582("PlayerAttitude", this.playerAttitude.name());
      tag.method_10582("OthersAttitude", this.othersAttitude.name());
      tag.method_10566("OwnRelation", this.ownRelation.serializeNBT());
      class_2487 relations = new class_2487();

      for(Map.Entry<String, FactionAttitude> entry : this.relations.entrySet()) {
         relations.method_10582((String)entry.getKey(), ((FactionAttitude)entry.getValue()).name());
      }

      if (relations.method_10546() > 0) {
         tag.method_10566("Relations", relations);
      }

      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Title")) {
         this.title = tag.method_10558("Title");
      }

      if (tag.method_10545("Visible")) {
         this.visible.deserializeNBT(tag.method_10580("Visible"));
      }

      if (tag.method_10545("Color")) {
         this.color = tag.method_10550("Color");
      }

      if (tag.method_10545("DefaultScore")) {
         this.score = tag.method_10550("DefaultScore");
      }

      if (tag.method_10545("PlayerAttitude")) {
         this.playerAttitude = FactionAttitude.get(tag.method_10558("PlayerAttitude"));
      }

      if (tag.method_10545("OthersAttitude")) {
         this.othersAttitude = FactionAttitude.get(tag.method_10558("OthersAttitude"));
      }

      if (tag.method_10545("OwnRelation")) {
         this.ownRelation.deserializeNBT(tag.method_10562("OwnRelation"));
      }

      if (tag.method_10545("Relations")) {
         class_2487 relations = tag.method_10562("Relations");

         for(String key : relations.method_10541()) {
            this.relations.put(key, FactionAttitude.get(relations.method_10558(key)));
         }
      }

   }
}
