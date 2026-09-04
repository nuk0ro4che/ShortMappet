package mchorse.mappet.network.client.huds;

import mchorse.mappet.Mappet;
import mchorse.mappet.api.huds.HUDScene;
import mchorse.mappet.client.RenderingHandler;
import mchorse.mappet.network.common.huds.PacketHUDScene;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;

public class ClientHandlerHUDScene extends ClientMessageHandler<PacketHUDScene> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketHUDScene message) {
      if (message.tag == null) {
         if (message.id.isEmpty()) {
            RenderingHandler.stage.scenes.clear();
         } else {
            RenderingHandler.stage.scenes.remove(message.id);
         }
      } else {
         HUDScene scene;
         if (Mappet.huds != null) {
            scene = (HUDScene)Mappet.huds.create(message.id, message.tag);
         } else {
            scene = new HUDScene();
            scene.setId(message.id);
            scene.deserializeNBT(message.tag);
         }

         RenderingHandler.stage.scenes.put(message.id, scene);
      }

   }
}
