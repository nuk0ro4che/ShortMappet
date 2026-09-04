package mchorse.mappet.api.expressions.functions.quests;

import mchorse.mappet.Mappet;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class QuestPresent extends SNFunction {
   public QuestPresent(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 2;
   }

   public double doubleValue() {
      try {
         String id = this.getArg(0).stringValue();
         String target = this.getArg(1).stringValue();
         MinecraftServer server = Mappet.expressions.getServer();

         for(class_3222 player : CommandBase.getPlayers(server, server, target)) {
            ICharacter character = Character.get(player);
            if (character != null && character.getQuests().getByName(id) != null) {
               return (double)1.0F;
            }
         }
      } catch (Exception var8) {
      }

      return (double)0.0F;
   }
}
