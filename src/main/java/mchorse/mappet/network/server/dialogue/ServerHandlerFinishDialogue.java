package mchorse.mappet.network.server.dialogue;

import mchorse.mappet.api.crafting.CraftingTable;
import mchorse.mappet.api.dialogues.Dialogue;
import mchorse.mappet.api.dialogues.DialogueContext;
import mchorse.mappet.api.dialogues.nodes.ReactionNode;
import mchorse.mappet.capabilities.character.Character;
import mchorse.mappet.capabilities.character.ICharacter;
import mchorse.mappet.network.common.dialogue.PacketFinishDialogue;
import mchorse.mappet.utils.WorldUtils;
import mchorse.mclib.network.ServerMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3222;

public class ServerHandlerFinishDialogue extends ServerMessageHandler<PacketFinishDialogue> {
   @Environment(EnvType.CLIENT)
   public void run(class_3222 player, PacketFinishDialogue message) {
      ICharacter character = Character.get(player);
      if (character != null) {
         character.setCraftingTable((CraftingTable)null);
         if (character.getDialogueContext() != null) {
            ReactionNode node = character.getDialogueContext().reactionNode;
            if (node != null && !node.sound.isEmpty()) {
               WorldUtils.stopSound(player, node.sound);
            }

            character.setDialogue((Dialogue)null, (DialogueContext)null);
         }
      }

   }
}
