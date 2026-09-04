package mchorse.mappet.api.factions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import mchorse.mappet.compat.INBTSerializable;
import net.minecraft.class_2487;
import net.minecraft.class_2499;

public class FactionRelation implements INBTSerializable<class_2487> {
   public List<Threshold> thresholds = new ArrayList();

   public FactionRelation() {
      this.thresholds.add(new Threshold(100, FactionAttitude.AGGRESSIVE, "Hostile", 15606306));
      this.thresholds.add(new Threshold(200, FactionAttitude.PASSIVE, "Neutral", 11184810));
      this.thresholds.add(new Threshold(1000, FactionAttitude.FRIENDLY, "Friendly", 2289186));
   }

   public FactionAttitude getAttitude(int score) {
      Threshold threshold = this.get(score);
      return threshold == null ? FactionAttitude.PASSIVE : threshold.attitude;
   }

   public Threshold get(int score) {
      for(Threshold threshold : this.thresholds) {
         if (score < threshold.score) {
            return threshold;
         }
      }

      return this.thresholds.isEmpty() ? null : (Threshold)this.thresholds.get(this.thresholds.size() - 1);
   }

   public void normalize() {
      this.thresholds.sort(Comparator.comparingInt((a) -> a.score));
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      class_2499 list = new class_2499();
      this.normalize();

      for(Threshold threshold : this.thresholds) {
         list.add(threshold.serializeNBT());
      }

      tag.method_10566("Thresholds", list);
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      this.thresholds.clear();
      if (tag.method_10545("Thresholds")) {
         class_2499 list = tag.method_10554("Thresholds", 10);

         for(int i = 0; i < list.size(); ++i) {
            class_2487 compound = list.method_10602(i);
            Threshold threshold = new Threshold();
            threshold.deserializeNBT(compound);
            this.thresholds.add(threshold);
         }
      }

      this.normalize();
   }

   public static class Threshold implements INBTSerializable<class_2487> {
      public int score;
      public FactionAttitude attitude;
      public String title;
      public int color;

      public Threshold() {
         this.attitude = FactionAttitude.PASSIVE;
         this.title = "";
         this.color = 16777215;
      }

      public Threshold(int score, FactionAttitude attitude, String title, int color) {
         this.attitude = FactionAttitude.PASSIVE;
         this.title = "";
         this.color = 16777215;
         this.score = score;
         this.attitude = attitude;
         this.title = title;
         this.color = color;
      }

      public class_2487 serializeNBT() {
         class_2487 tag = new class_2487();
         tag.method_10569("Score", this.score);
         tag.method_10582("Attitude", this.attitude.name());
         tag.method_10582("Title", this.title);
         tag.method_10569("Color", this.color);
         return tag;
      }

      public void deserializeNBT(class_2487 tag) {
         this.score = tag.method_10550("Score");
         this.attitude = FactionAttitude.get(tag.method_10558("Attitude"));
         this.title = tag.method_10558("Title");
         this.color = tag.method_10550("Color");
      }
   }
}
