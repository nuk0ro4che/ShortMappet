package mchorse.mappet.utils;

import mchorse.mappet.api.conditions.Checker;
import mchorse.mappet.compat.INBTSerializable;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import net.minecraft.class_2487;
import net.minecraft.class_2520;

public class ConditionModel implements INBTSerializable<class_2487> {
   public AbstractMorph morph = this.getDefaultMorph();
   public Checker checker = new Checker();

   public AbstractMorph getDefaultMorph() {
      class_2487 tag = new class_2487();
      tag.method_10582("Name", "blockbuster.fred");
      return MorphManager.INSTANCE.morphFromNBT(tag);
   }

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10566("checker", this.checker.serializeNBT());
      class_2487 morph = new class_2487();
      this.morph.toNBT(morph);
      tag.method_10566("morph", morph);
      return tag;
   }

   public void deserializeNBT(class_2487 nbt) {
      this.checker.deserializeNBT((class_2520)nbt.method_10562("checker"));
      this.morph = MorphManager.INSTANCE.morphFromNBT(nbt.method_10562("morph"));
   }

   public String toString() {
      String var10000 = this.morph.name;
      return "ConditionModel[morph_name:" + var10000 + ",condition:" + this.checker.toString() + "]";
   }
}
