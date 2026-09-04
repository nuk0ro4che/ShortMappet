package mchorse.mappet.network.client.quests;

import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.common.quests.PacketQuests;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerQuests extends ClientMessageHandler<PacketQuests> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketQuests message) {
      ICharacter character = Character.get(player);
      if (character != null) {
         character.getQuests().quests.clear();
         character.getQuests().quests.putAll(message.quests);
      }

   }
}
