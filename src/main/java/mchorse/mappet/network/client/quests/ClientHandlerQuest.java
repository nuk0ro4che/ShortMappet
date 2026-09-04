package mchorse.mappet.network.client.quests;

import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.common.quests.PacketQuest;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerQuest extends ClientMessageHandler<PacketQuest> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketQuest message) {
      ICharacter character = Character.get(player);
      if (character != null) {
         if (message.quest == null) {
            character.getQuests().quests.remove(message.id);
         } else {
            character.getQuests().quests.put(message.id, message.quest);
         }
      }

   }
}
