package mchorse.mappet.api.conditions.blocks;

import mchorse.mappet.api.states.States;
import mchorse.mappet.api.utils.DataContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class StateConditionBlock extends PropertyConditionBlock {
   public boolean evaluateBlock(DataContext context) {
      States states = this.target.getStates(context);
      if (states == null) {
         return false;
      } else if (this.comparison.comparison.isString) {
         return states.isString(this.id) ? this.compareString(states.getString(this.id)) : this.compareString(String.valueOf(states.getNumber(this.id)));
      } else {
         return this.compare(states.getNumber(this.id));
      }
   }

   @Environment(EnvType.CLIENT)
   public String stringify() {
      return this.comparison.stringify(this.id);
   }
}
