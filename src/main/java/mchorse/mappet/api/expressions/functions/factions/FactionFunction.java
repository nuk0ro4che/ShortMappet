package mchorse.mappet.api.expressions.functions.factions;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.factions.Faction;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;
import net.minecraft.class_3222;

public abstract class FactionFunction extends SNFunction {
   public FactionFunction(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   protected Faction getFaction(String id) {
      return (Faction)Mappet.factions.load(id);
   }

   public int getRequiredArguments() {
      return 2;
   }

   public double doubleValue() {
      try {
         String id = this.getArg(0).stringValue();
         String target = this.getArg(1).stringValue();
         class_3222 player = CommandBase.getPlayer(Mappet.expressions.getServer(), Mappet.expressions.getServer(), target);
         ICharacter character = Character.get(player);
         if (character != null) {
            return this.apply(id, character);
         }
      } catch (Exception var5) {
      }

      return (double)0.0F;
   }

   protected abstract double apply(String var1, ICharacter var2);
}
