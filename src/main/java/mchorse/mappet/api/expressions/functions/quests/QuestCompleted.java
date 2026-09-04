package mchorse.mappet.api.expressions.functions.quests;

import mchorse.mappet.Mappet;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.compat.CommandBase;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class QuestCompleted extends SNFunction {
   public QuestCompleted(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 2;
   }

   public double doubleValue() {
      try {
         String target = this.getArg(1).stringValue();
         String id = this.getArg(0).stringValue();
         if (target.equals("~")) {
            return Mappet.states.wasQuestCompleted(id) ? (double)1.0F : (double)0.0F;
         }

         MinecraftServer server = Mappet.expressions.getServer();

         for(class_3222 player : CommandBase.getPlayers(server, server, target)) {
            ICharacter character = Character.get(player);
            if (character != null && character.getStates().wasQuestCompleted(id)) {
               return (double)1.0F;
            }
         }
      } catch (Exception var8) {
      }

      return (double)0.0F;
   }
}
