package mchorse.mappet.api.expressions.functions.factions;

import mchorse.mappet.api.factions.Faction;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mclib.math.IValue;

public class FactionFriendly extends FactionFunction {
   public FactionFriendly(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   protected double apply(String id, ICharacter character) {
      Faction faction = this.getFaction(id);
      return faction.get(character.getStates()).isFriendly() ? (double)1.0F : (double)0.0F;
   }
}
