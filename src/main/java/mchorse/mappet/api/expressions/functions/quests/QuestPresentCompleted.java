package mchorse.mappet.api.expressions.functions.quests;

import mchorse.mappet.Mappet;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mclib.math.IValue;
import mchorse.mclib.math.functions.SNFunction;
import net.minecraft.class_3222;
import net.minecraft.server.MinecraftServer;

public class QuestPresentCompleted extends SNFunction {
   public QuestPresentCompleted(IValue[] values, String name) throws Exception {
      super(values, name);
   }

   public int getRequiredArguments() {
      return 1;
   }

   public double doubleValue() {
      try {
         String id = this.getArg(0).stringValue();
         if (Mappet.states.wasQuestCompleted(id)) {
            return (double)1.0F;
         }

         MinecraftServer server = Mappet.expressions.getServer();

         for(class_3222 player : server.method_3760().method_14571()) {
            ICharacter character = Character.get(player);
            if (character != null && (character.getStates().wasQuestCompleted(id) || character.getQuests().getByName(id) != null)) {
               return (double)1.0F;
            }
         }
      } catch (Exception var6) {
      }

      return (double)0.0F;
   }
}
