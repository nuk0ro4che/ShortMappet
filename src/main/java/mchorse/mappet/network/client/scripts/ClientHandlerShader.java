package mchorse.mappet.network.client.scripts;

import mchorse.mappet.client.shaders.ClientShaderRuntime;
import mchorse.mappet.network.common.scripts.PacketShader;
import mchorse.mclib.network.ClientMessageHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_746;


public class ClientHandlerShader extends ClientMessageHandler<PacketShader> {
   @Environment(EnvType.CLIENT)
   public void run(class_746 player, PacketShader message) {
      if (message.remove) {
         ClientShaderRuntime.remove(message.target);
      } else if (message.shader != null) {
         

         message.shader.enabled = true;
         message.shader.world = true;
         ClientShaderRuntime.apply(message.shader, message.target);
      }
   }
}
