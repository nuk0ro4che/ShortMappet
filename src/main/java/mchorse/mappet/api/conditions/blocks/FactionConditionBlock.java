package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.factions.Faction;
import mchorse.mappet.api.factions.FactionAttitude;
import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.DataContext;
import mchorse.mappet.api.utils.TargetMode;
import mchorse.mappet.utils.EnumUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1074;
import net.minecraft.class_2487;

public class FactionConditionBlock extends PropertyConditionBlock {
   public FactionCheck faction;

   public FactionConditionBlock() {
      this.faction = FactionConditionBlock.FactionCheck.SCORE;
   }

   protected TargetMode getDefaultTarget() {
      return TargetMode.SUBJECT;
   }

   public boolean evaluateBlock(DataContext context) {
      if (this.target.mode != TargetMode.GLOBAL) {
         States states = this.target.getStates(context);
         if (states == null) {
            return false;
         } else if (this.faction == FactionConditionBlock.FactionCheck.SCORE) {
            if (!states.hasFaction(this.id)) {
               return false;
            } else {
               return this.comparison.comparison.isString ? this.compareString(String.valueOf(states.getFactionScore(this.id))) : this.compare((double)states.getFactionScore(this.id));
            }
         } else {
            Faction faction = (Faction)Mappet.factions.load(this.id);
            if (faction == null) {
               return false;
            } else {
               return faction.get(states) == this.faction.attitude;
            }
         }
      } else {
         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      if (this.faction == FactionConditionBlock.FactionCheck.SCORE) {
         return this.comparison.stringify(this.id);
      } else if (this.faction == FactionConditionBlock.FactionCheck.AGGRESSIVE) {
         return class_1074.method_4662("mappet.gui.conditions.faction.is_aggressive", new Object[]{this.id});
      } else {
         return this.faction == FactionConditionBlock.FactionCheck.PASSIVE ? class_1074.method_4662("mappet.gui.conditions.faction.is_passive", new Object[]{this.id}) : class_1074.method_4662("mappet.gui.conditions.faction.is_friendly", new Object[]{this.id});
      }
   }

   public void serializeNBT(class_2487 tag) {
      super.serializeNBT(tag);
      tag.method_10569("Faction", this.faction.ordinal());
   }

   public void deserializeNBT(class_2487 tag) {
      super.deserializeNBT(tag);
      this.faction = (FactionCheck)EnumUtils.getValue(tag.method_10550("Faction"), FactionConditionBlock.FactionCheck.values(), FactionConditionBlock.FactionCheck.SCORE);
   }

   public static enum FactionCheck {
      AGGRESSIVE(FactionAttitude.AGGRESSIVE),
      PASSIVE(FactionAttitude.PASSIVE),
      FRIENDLY(FactionAttitude.FRIENDLY),
      SCORE((FactionAttitude)null);

      public final FactionAttitude attitude;

      private FactionCheck(FactionAttitude attitude) {
         this.attitude = attitude;
      }
      private static FactionCheck[] $values() {
         return new FactionCheck[]{AGGRESSIVE, PASSIVE, FRIENDLY, SCORE};
      }
   }
}
