package mchorse.mappet.api.shaders;

import java.util.LinkedHashMap;
import java.util.Map;
import mchorse.mappet.api.utils.AbstractData;
import net.minecraft.class_2487;





public class ShaderFile extends AbstractData {
   public static final String DEFAULT_VERTEX = "#version 120\n\nvarying vec2 texCoord;\n\nvoid main() {\n    texCoord = gl_MultiTexCoord0.xy;\n    gl_Position = gl_Vertex;\n}";
   public static final String DEFAULT_FRAGMENT = "#version 120\n\nuniform sampler2D colorTex;\nvarying vec2 texCoord;\n\nvoid main() {\n    gl_FragColor = texture2D(colorTex, texCoord);\n}";

   public boolean enabled = true;
   public boolean global;
   public boolean depthBuffer = true;
   public boolean world = true;
   public boolean hideInFirstPerson;
   public boolean renderOnHand;
   public int renderStage;
   public String blendMode = "additive";
   public int passes = 1;
   public float scale = 0.5F;
   
   public int duration;
   public boolean shadowMap;
   public int shadowResolution = 1024;
   public int shadowDistance = 128;
   public String vertex = DEFAULT_VERTEX;
   public String fragment = DEFAULT_FRAGMENT;

   /**
    * Default shader parameters (uniforms and config overrides). Runtime
    * parameters supplied through {@code player.applyShader(id, params)} are
    * merged on top of this map in a render-time copy.
    */
   public Map<String, Object> parameters = new LinkedHashMap<String, Object>();

   public class_2487 serializeNBT() {
      class_2487 tag = new class_2487();
      tag.method_10556("Enabled", this.enabled);
      tag.method_10556("Global", this.global);
      tag.method_10556("DepthBuffer", this.depthBuffer);
      tag.method_10556("World", this.world);
      tag.method_10556("HideInFirstPerson", this.hideInFirstPerson);
      tag.method_10556("RenderOnHand", this.renderOnHand);
      tag.method_10569("RenderStage", this.renderStage);
      tag.method_10582("BlendMode", this.blendMode == null ? "additive" : this.blendMode);
      tag.method_10569("Passes", this.passes);
      tag.method_10548("Scale", this.scale);
      tag.method_10569("Duration", this.duration);
      tag.method_10556("ShadowMap", this.shadowMap);
      tag.method_10569("ShadowResolution", this.shadowResolution);
      tag.method_10569("ShadowDistance", this.shadowDistance);
      tag.method_10582("Vertex", this.vertex == null ? "" : this.vertex);
      tag.method_10582("Fragment", this.fragment == null ? "" : this.fragment);
      tag.method_10566("Parameters", ShaderParameters.toNbt(this.parameters));
      return tag;
   }

   public void deserializeNBT(class_2487 tag) {
      if (tag.method_10545("Enabled")) this.enabled = tag.method_10577("Enabled");
      if (tag.method_10545("Global")) this.global = tag.method_10577("Global");
      if (tag.method_10545("DepthBuffer")) this.depthBuffer = tag.method_10577("DepthBuffer");
      if (tag.method_10545("World")) this.world = tag.method_10577("World");
      if (tag.method_10545("HideInFirstPerson")) this.hideInFirstPerson = tag.method_10577("HideInFirstPerson");
      if (tag.method_10545("RenderOnHand")) this.renderOnHand = tag.method_10577("RenderOnHand");
      if (tag.method_10545("RenderStage")) this.renderStage = tag.method_10550("RenderStage");
      if (tag.method_10545("BlendMode")) this.blendMode = tag.method_10558("BlendMode");
      if (tag.method_10545("Passes")) this.passes = Math.max(1, tag.method_10550("Passes"));
      if (tag.method_10545("Scale")) this.scale = Math.max(0.05F, tag.method_10583("Scale"));
      if (tag.method_10545("Duration")) this.duration = Math.max(0, tag.method_10550("Duration"));
      if (tag.method_10545("ShadowMap")) this.shadowMap = tag.method_10577("ShadowMap");
      if (tag.method_10545("ShadowResolution")) this.shadowResolution = Math.max(16, tag.method_10550("ShadowResolution"));
      if (tag.method_10545("ShadowDistance")) this.shadowDistance = Math.max(1, tag.method_10550("ShadowDistance"));
      if (tag.method_10545("Vertex")) this.vertex = tag.method_10558("Vertex");
      if (tag.method_10545("Fragment")) this.fragment = tag.method_10558("Fragment");
      if (tag.method_10545("Parameters")) this.parameters = ShaderParameters.fromNbt(tag.method_10562("Parameters"));
   }

   /**
    * Creates a render-time copy. The id is preserved so status checks keep
    * working against the original file instance.
    */
   public ShaderFile copy() {
      ShaderFile shader = new ShaderFile();
      shader.setId(this.getId());
      shader.enabled = this.enabled;
      shader.global = this.global;
      shader.depthBuffer = this.depthBuffer;
      shader.world = this.world;
      shader.hideInFirstPerson = this.hideInFirstPerson;
      shader.renderOnHand = this.renderOnHand;
      shader.renderStage = this.renderStage;
      shader.blendMode = this.blendMode;
      shader.passes = this.passes;
      shader.scale = this.scale;
      shader.duration = this.duration;
      shader.shadowMap = this.shadowMap;
      shader.shadowResolution = this.shadowResolution;
      shader.shadowDistance = this.shadowDistance;
      shader.vertex = this.vertex;
      shader.fragment = this.fragment;
      shader.parameters = ShaderParameters.copy(this.parameters);
      return shader;
   }
}
