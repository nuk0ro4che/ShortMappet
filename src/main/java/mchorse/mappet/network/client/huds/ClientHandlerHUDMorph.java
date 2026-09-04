package mchorse.mappet.network.client.huds;

import mchorse.mappet.api.huds.HUDMorph;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.network.common.huds.PacketHUDMorph;
import mchorse.mclib.network.ClientMessageHandler;
import mchorse.metamorph.api.MorphManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerHUDMorph extends ClientMessageHandler<PacketHUDMorph> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketHUDMorph message) {
      HUDScene scene = (HUDScene)RenderingHandler.stage.scenes.get(message.id);
      if (scene != null && message.index >= 0 && message.index < scene.morphs.size()) {
         HUDMorph morph = (HUDMorph)scene.morphs.get(message.index);
         morph.morph.set(MorphManager.INSTANCE.morphFromNBT(message.morph));
      }

   }
}
