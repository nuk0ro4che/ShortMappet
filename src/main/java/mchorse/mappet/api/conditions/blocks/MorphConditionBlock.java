package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.models.IMorphProvider;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.capabilities.morphing.Morphing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2487;

public class MorphConditionBlock extends TargetConditionBlock {
   public class_2487 morph;
   public boolean onlyName;
   private AbstractMorph compiledMorph;

   protected TargetMode getDefaultTarget() {
      return TargetMode.PLAYER;
   }

   protected boolean evaluateBlock(DataContext context) {
      class_1297 entity = this.target.getEntity(context);
      AbstractMorph morph = this.getMorph(entity);
      if (morph == null && this.morph == null) {
         return true;
      } else if (morph != null && this.morph != null) {
         if (this.onlyName) {
            return morph.name.equals(this.morph.method_10558("Name"));
         } else {
            if (this.compiledMorph == null) {
               this.compiledMorph = MorphManager.INSTANCE.morphFromNBT(this.morph);
            }

            return morph.equals(this.compiledMorph);
         }
      } else {
         return false;
      }
   }

   private AbstractMorph getMorph(class_1297 entity) {
      AbstractMorph morph = null;
      if (entity instanceof IMorphProvider) {
         morph = ((IMorphProvider)entity).getMorph();
      } else if (entity instanceof class_1657) {
         morph = Morphing.get((class_1657)entity).getCurrentMorph();
      }

      return morph;
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      return this.morph == null ? class_1074.method_4662("mappet.gui.conditions.morph.no_morph", new Object[0]) : class_1074.method_4662("mappet.gui.conditions.morph.string", new Object[]{this.morph.method_10558("Name")});
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      if (this.morph != null) {
         tag.method_10566("Morph", this.morph);
      }

      tag.method_10556("OnlyName", this.onlyName);
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.morph = null;
      this.compiledMorph = null;
      if (tag.method_10573("Morph", 10)) {
         this.morph = tag.method_10562("Morph");
      }

      this.onlyName = tag.method_10577("OnlyName");
   }
}
