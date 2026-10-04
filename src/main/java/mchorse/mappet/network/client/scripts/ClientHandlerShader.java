package mchorse.mappet.network.client.scripts;

import mchorse.mappet.Mappet;
import mchorse.mappet.MappetClient;
import mchorse.mappet.api.shaders.ShaderFile;
import mchorse.mappet.api.shaders.ShaderManager;
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
         String id = message.id == null ? "" : message.id.trim();

         if (id.isEmpty() || id.equals(ClientShaderRuntime.getAppliedName(message.target))) {
            ClientShaderRuntime.remove(message.target);
         }

         return;
      }

      ShaderFile shader = this.resolve(message.id);

      if (shader == null) {
         Mappet.LOGGER.warn("Shader \"{}\" was not found on the client, skipping", message.id);

         return;
      }

      shader.enabled = true;
      shader.world = true;
      ClientShaderRuntime.apply(shader, message.target);
   }

   private ShaderFile resolve(String id) {
      if (id == null || id.trim().isEmpty()) {
         return null;
      }

      String key = id.trim();

      ShaderFile shader = this.load(Mappet.shaders, key);

      if (shader == null) {
         shader = this.load(MappetClient.clientShaders, key);
      }

      return shader;
   }

   private ShaderFile load(ShaderManager manager, String id) {
      return manager == null ? null : manager.load(id);
   }
}