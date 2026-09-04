package mchorse.mappet.network.server.factions;

import java.util.HashMap;
import java.util.Map;
import mchorse.mappet.Mappet;
import mchorse.mappet.api.factions.Faction;
import mchorse.mappet.api.states.States;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.Dispatcher;
import mchorse.mappet.network.common.factions.PacketFactions;
import mchorse.mappet.network.common.factions.PacketRequestFactions;
import mchorse.mclib.network.ServerMessageHandler;
import net.minecraft.class_3222;

public class ServerHandlerRequestFactions extends ServerMessageHandler<PacketRequestFactions> {
   public static void collectFactions(class_3222 player, States states) {
      Map<String, Double> statesData = new HashMap();
      Map<String, Faction> factions = new HashMap();

      for(Map.Entry<String, Object> entry : states.values.entrySet()) {
         String key = (String)entry.getKey();
         if (key.startsWith("factions.") && entry.getValue() instanceof Number) {
            statesData.put(key.substring("factions.".length()), ((Number)entry.getValue()).doubleValue());
         }
      }

      if (!statesData.isEmpty()) {
         for(String key : statesData.keySet()) {
            Faction faction = (Faction)Mappet.factions.load(key);
            if (faction != null && faction.isVisible(player)) {
               factions.put(key, faction);
            }
         }

         if (!factions.isEmpty()) {
            Dispatcher.sendTo(new PacketFactions(factions, statesData), player);
         }
      }

   }

   public void run(class_3222 player, PacketRequestFactions message) {
      ICharacter character = Character.get(player);
      if (character != null) {
         collectFactions(player, character.getStates());
      }

   }
}
