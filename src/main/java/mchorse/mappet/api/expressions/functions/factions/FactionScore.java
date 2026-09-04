package mchorse.mappet.api.expressions.functions.factions;

import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mclib.math.IValue;

public class FactionScore extends FactionFunction {
   public FactionScore(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   protected double apply(String id, ICharacter character) {
      return (double)character.getStates().getFactionScore(id);
   }
}
