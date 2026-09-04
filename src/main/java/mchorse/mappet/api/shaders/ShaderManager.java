package mchorse.mappet.api.shaders;

import java.io.File;
import mchorse.mappet.api.utils.manager.BaseManager;
import net.minecraft.class_2487;

public class ShaderManager extends BaseManager<ShaderFile> {
   public ShaderManager(File folder) {
      super(folder);
   }

   protected ShaderFile createData(String id, class_2487 tag) {
      ShaderFile shader = new ShaderFile();
      if (tag != null) {
         shader.deserializeNBT(tag);
      }
      return shader;
   }
}
