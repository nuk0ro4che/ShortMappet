package mchorse.mappet.client.shaders;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mchorse.mappet.api.shaders.ShaderFile;
import mchorse.mappet.api.shaders.ShaderParameters;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;






@Environment(EnvType.CLIENT)
public final class ClientShaderRuntime {
   public static final int TARGET_SCREEN = 0;
   public static final int TARGET_INTERFACE = 1;
   public static final int TARGET_HUD = 2;
   private static final int TARGET_HAND = 3;

   private static final ShaderChannel screen = new ShaderChannel(TARGET_SCREEN, false);
   private static final ShaderChannel interfaceChannel = new ShaderChannel(TARGET_INTERFACE, false);
   private static final ShaderChannel hud = new ShaderChannel(TARGET_HUD, false);
   
   /* Only the hand layer needs a stencil attachment, for TACZ scope lenses. */
   private static final ShaderChannel hand = new ShaderChannel(TARGET_HAND, true);
   private static final ShaderChannel handScratch = new ShaderChannel(TARGET_HAND, false);
   private static final String MASK_VERTEX = "#version 120\n\nvarying vec2 texCoord;\n\nvoid main() {\n    texCoord = gl_MultiTexCoord0.xy;\n    gl_Position = gl_Vertex;\n}";
   private static final String MASK_FRAGMENT = "#version 120\n\nuniform sampler2D colorTex;\nuniform sampler2D maskTex;\nvarying vec2 texCoord;\n\nvoid main() {\n    vec4 color = texture2D(colorTex, texCoord);\n    float mask = clamp(texture2D(maskTex, texCoord).a, 0.0, 1.0);\n    gl_FragColor = vec4(color.rgb * mask, mask);\n}";
   private static int maskProgram;
   private static int copiedScreenTexture;
   private static int fullscreenVao;
   private static int fullscreenVbo;
   private static String error = "";

   private ClientShaderRuntime() {
   }

   
   public static boolean apply(ShaderFile shader) {
      return apply(shader, TARGET_SCREEN, null);
   }

   
   public static boolean applyInterface(ShaderFile shader) {
      return apply(shader, TARGET_INTERFACE, null);
   }

   
   public static boolean applyHud(ShaderFile shader) {
      return apply(shader, TARGET_HUD, null);
   }

   public static boolean apply(ShaderFile shader, int target) {
      return apply(shader, target, null);
   }

   /**
    * Applies the shader with runtime parameters. The parameters map may contain
    * GLSL uniforms and reserved config overrides (for example
    * {@code renderOnHand} or {@code $blendMode}).
    */
   public static boolean apply(ShaderFile shader, int target, Map<String, Object> parameters) {
      if (shader == null) {
         setError("Шейдер не выбран");
         return false;
      }

      Map<String, Object> normalized = ShaderParameters.normalize(parameters);
      boolean[] applied = new boolean[]{false};

      runOnRenderThread(() -> {
         if (!GL.getCapabilities().OpenGL20 || !GL.getCapabilities().OpenGL30) {
            setError("Видеодрайвер не поддерживает современный OpenGL, нужный для экранного шейдера");
            return;
         }

         ShaderFile effective = ShaderParameters.applyTo(shader, normalized);
         applied[0] = applyToChannel(shader, effective, getChannel(target));

         if (target == TARGET_SCREEN) {
            if (effective.renderOnHand && applied[0]) {
               applyToChannel(shader, effective, hand);
            } else if (!effective.renderOnHand) {
               removeChannel(hand);
            }
         }
      });

      return applied[0];
   }

   /**
    * Runs the action on the render thread.
    *
    * <p>Every {@code gl*} call in this class needs the context of the render
    * thread. Packets and disconnect events are handled on the network thread,
    * where such a call is a fatal LWJGL error that aborts the JVM, so anything
    * that does not already run on the render thread has to be deferred.</p>
    */
   private static void runOnRenderThread(Runnable action) {
      class_310 client = class_310.method_1551();

      if (client == null) {
         return;
      }

      if (client.method_18854()) {
         action.run();
      } else {
         client.execute(action);
      }
   }

   private static boolean applyToChannel(ShaderFile original, ShaderFile shader, ShaderChannel channel) {
      int vertexShader = 0;
      int fragmentShader = 0;
      int compiledProgram = 0;
      try {
         vertexShader = compile(GL20.GL_VERTEX_SHADER, shader.vertex, "Вершинный шейдер");
         fragmentShader = compile(GL20.GL_FRAGMENT_SHADER, shader.fragment, "Фрагментный шейдер");
         compiledProgram = GL20.glCreateProgram();
         GL20.glAttachShader(compiledProgram, vertexShader);
         GL20.glAttachShader(compiledProgram, fragmentShader);
         GL20.glLinkProgram(compiledProgram);
         if (GL20.glGetProgrami(compiledProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new IllegalArgumentException("Ошибка связывания:\n" + GL20.glGetProgramInfoLog(compiledProgram, 8192));
         }

         int oldProgram = channel.program;
         channel.program = compiledProgram;
         compiledProgram = 0;
         channel.active = original;
         channel.effective = shader;
         channel.activeName = shader.getId();
         channel.uniformTypes = collectUniformTypes(channel.program);
         error = "";
         if (oldProgram != 0) {
            GL20.glDeleteProgram(oldProgram);
         }
         return true;
      } catch (Throwable throwable) {
         String message = throwable.getMessage();
         setError(message == null || message.trim().isEmpty() ? throwable.getClass().getSimpleName() : message);
         return false;
      } finally {
         if (vertexShader != 0) GL20.glDeleteShader(vertexShader);
         if (fragmentShader != 0) GL20.glDeleteShader(fragmentShader);
         if (compiledProgram != 0) GL20.glDeleteProgram(compiledProgram);
      }
   }

   private static int compile(int type, String source, String title) {
      if (source == null || source.trim().isEmpty()) {
         throw new IllegalArgumentException(title + ": код пустой");
      }
      int shader = GL20.glCreateShader(type);
      GL20.glShaderSource(shader, source);
      GL20.glCompileShader(shader);
      if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
         String log = GL20.glGetShaderInfoLog(shader, 8192);
         GL20.glDeleteShader(shader);
         throw new IllegalArgumentException(title + ":\n" + log);
      }
      return shader;
   }

   
   public static void remove() {
      remove(TARGET_SCREEN);
   }

   
   public static void removeInterface() {
      remove(TARGET_INTERFACE);
   }

   
   public static void removeHud() {
      remove(TARGET_HUD);
   }

   public static void remove(int target) {
      runOnRenderThread(() -> {
         removeChannel(getChannel(target));

         if (target == TARGET_SCREEN) {
            removeChannel(hand);
         }
      });
   }

   private static void removeChannel(ShaderChannel channel) {
      runOnRenderThread(() -> {
         channel.active = null;
         channel.effective = null;
         channel.activeName = "";
         channel.uniformTypes = new HashMap<String, Integer>();
         error = "";

         if (channel.program != 0) {
            GL20.glDeleteProgram(channel.program);
            channel.program = 0;
         }
      });
   }

   public static String getAppliedName(int target) {
      ShaderChannel channel = getChannel(target);

      return channel.activeName == null ? "" : channel.activeName;
   }

   public static boolean isApplied(ShaderFile shader) {
      return shader != null && screen.program != 0 && screen.active == shader;
   }

   public static String getStatus(ShaderFile shader) {
      if (!error.isEmpty()) {
         return error;
      }
      if (shader != null && isApplied(shader)) {
         return "Применён: " + screen.activeName + (hand.active == shader ? "; рука включена" : "");
      }
      if (shader != null && hand.program != 0 && hand.active == shader) {
         return "Применён только к руке: " + hand.activeName;
      }
      return "Не применён";
   }

   public static boolean hasError() {
      return !error.isEmpty();
   }

   
   public static void render(float tickDelta) {
      if (!shouldRender(screen, true)) {
         return;
      }

      class_310 client = class_310.method_1551();
      if (client.field_1687 == null) {
         return;
      }

      try {
         int width = getWidth(client);
         int height = getHeight(client);
         int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
         GL13.glActiveTexture(GL13.GL_TEXTURE0);
         int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
         try {
            ensureCopiedScreenTexture();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, copiedScreenTexture);
            GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, width, height, 0);
            renderTexture(screen, copiedScreenTexture, width, height);
         } finally {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
            GL13.glActiveTexture(previousActiveTexture);
         }
      } catch (Throwable throwable) {
         handleRenderFailure(screen, throwable);
      }
   }

   
   public static void beginHud() {
      beginCapture(hud);
   }

   
   public static void endHud(float tickDelta) {
      endCapture(hud);
   }

   
   public static void beginHand() {
      if (hand.effective != null && hand.effective.renderOnHand) {
         beginCapture(hand, hand);
      }
   }

   
   public static void endHand(float tickDelta) {
      endCapture(hand, hand, true);
   }

   
   public static void beginInterface() {
      beginCapture(interfaceChannel);
   }

   
   public static void endInterface(float tickDelta) {
      endCapture(interfaceChannel);
   }

   private static boolean shouldRender(ShaderChannel channel, boolean honorWorldFlag) {
      ShaderFile shader = channel.effective;

      return channel.program != 0 && channel.active != null && shader != null && shader.enabled
            && (!honorWorldFlag || shader.world) && !isHiddenByF1(channel);
   }

   
   private static boolean isHiddenByF1(ShaderChannel channel) {
      return channel.effective != null && channel.effective.hideInFirstPerson
            && class_310.method_1551().field_1690.field_1842;
   }

   private static void beginCapture(ShaderChannel channel) {
      beginCapture(channel, channel);
   }

   private static void beginCapture(ShaderChannel capture, ShaderChannel effect) {
      if (!shouldRender(effect, false) || capture.capturing) {
         return;
      }

      try {
         class_310 client = class_310.method_1551();
         int width = getWidth(client);
         int height = getHeight(client);

         capture.previousDrawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
         capture.previousReadFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
         GL11.glGetIntegerv(GL11.GL_VIEWPORT, capture.previousViewport);

         ensureCaptureBuffer(capture, width, height);
         GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, capture.framebuffer);
         GL11.glViewport(0, 0, width, height);
         clearCaptureBuffer();
         capture.capturing = true;
      } catch (Throwable throwable) {
         restoreCaptureState(capture);
         handleCaptureFailure(capture, throwable);
      }
   }

   private static void endCapture(ShaderChannel channel) {
      endCapture(channel, channel);
   }

   private static void endCapture(ShaderChannel capture, ShaderChannel effect) {
      endCapture(capture, effect, false);
   }

   private static void endCapture(ShaderChannel capture, ShaderChannel effect, boolean masked) {
      if (!capture.capturing) {
         return;
      }

      capture.capturing = false;
      try {
         class_310 client = class_310.method_1551();
         int width = getWidth(client);
         int height = getHeight(client);
         GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, capture.previousDrawFramebuffer);
         GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, capture.previousReadFramebuffer);

         if (masked) {
            /* The effect paints the whole frame, so it is applied to the hand layer first and then composited through the captured hand alpha, otherwise the effect's own alpha (usually 1.0) erases the world */
            ensureCaptureBuffer(handScratch, width, height);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, handScratch.framebuffer);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, handScratch.framebuffer);
            GL11.glViewport(0, 0, width, height);
            renderTexture(effect, capture.texture, width, height);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, capture.previousDrawFramebuffer);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, capture.previousReadFramebuffer);
            GL11.glViewport(capture.previousViewport[0], capture.previousViewport[1], capture.previousViewport[2], capture.previousViewport[3]);
            compositeHand(handScratch.texture, capture.texture, width, height);
         } else {
            GL11.glViewport(capture.previousViewport[0], capture.previousViewport[1], capture.previousViewport[2], capture.previousViewport[3]);
            renderTexture(effect, capture.texture, width, height);
         }
      } catch (Throwable throwable) {
         handleRenderFailure(effect, throwable);
      } finally {
         restoreCaptureState(capture);
         capture.previousDrawFramebuffer = 0;
         capture.previousReadFramebuffer = 0;
      }
   }

   private static void restoreCaptureState(ShaderChannel capture) {
      GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, capture.previousDrawFramebuffer);
      GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, capture.previousReadFramebuffer);

      if (capture.previousViewport[2] > 0 && capture.previousViewport[3] > 0) {
         GL11.glViewport(capture.previousViewport[0], capture.previousViewport[1], capture.previousViewport[2], capture.previousViewport[3]);
      }
   }

   private static int getWidth(class_310 client) {
      return Math.max(1, client.method_22683().method_4489());
   }

   private static int getHeight(class_310 client) {
      return Math.max(1, client.method_22683().method_4506());
   }

   private static void ensureCopiedScreenTexture() {
      if (copiedScreenTexture == 0) {
         copiedScreenTexture = GL11.glGenTextures();
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, copiedScreenTexture);
         configureTexture();
      }
   }

   private static void ensureCaptureBuffer(ShaderChannel channel, int width, int height) {
      if (channel.framebuffer == 0) {
         channel.framebuffer = GL30.glGenFramebuffers();
         channel.texture = GL11.glGenTextures();
         channel.depthStencilBuffer = GL30.glGenRenderbuffers();
      }
      if (channel.width == width && channel.height == height) {
         return;
      }

      int previousDrawFramebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
      int previousReadFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
      int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
      int previousRenderbuffer = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
      try {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, channel.texture);
         configureTexture();
         GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0L);
         GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, channel.framebuffer);
         GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, channel.texture, 0);
         GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, channel.depthStencilBuffer);
         if (channel.stencil) {
            /* TACZ scope lenses require a stencil attachment on the hand capture FBO.
             * The internal format must match Forge's stencil-enabled render targets
             * exactly (GL_DEPTH32F_STENCIL8), otherwise glBlitFramebuffer rejects the
             * stencil copy with GL_INVALID_OPERATION and the scope mask never reaches
             * TACZ. */
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH32F_STENCIL8, width, height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, channel.depthStencilBuffer);
         } else {
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT24, width, height);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, channel.depthStencilBuffer);
         }
         if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Не удалось создать буфер для UI/HUD шейдера");
         }
         channel.width = width;
         channel.height = height;
      } finally {
         GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, previousRenderbuffer);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
         GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, previousDrawFramebuffer);
         GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, previousReadFramebuffer);
      }
   }

   private static void configureTexture() {
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
   }

   /**
    * Clears the capture FBO even when scissor, color mask or depth/stencil
    * write masks are currently restricting writes.
    */
   private static void clearCaptureBuffer() {
      boolean scissor = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
      boolean[] colorMask = new boolean[4];
      getColorMask(colorMask);
      boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
      int frontStencilMask = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
      int backStencilMask = GL11.glGetInteger(GL20.GL_STENCIL_BACK_WRITEMASK);
      float[] clearColor = new float[4];
      GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE, clearColor);

      try {
         if (scissor) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
         }

         GL11.glColorMask(true, true, true, true);
         GL11.glDepthMask(true);
         GL20.glStencilMaskSeparate(GL11.GL_FRONT, 0xFF);
         GL20.glStencilMaskSeparate(GL11.GL_BACK, 0xFF);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
      } finally {
         GL11.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
         GL20.glStencilMaskSeparate(GL11.GL_FRONT, frontStencilMask);
         GL20.glStencilMaskSeparate(GL11.GL_BACK, backStencilMask);
         GL11.glDepthMask(depthMask);
         GL11.glColorMask(colorMask[0], colorMask[1], colorMask[2], colorMask[3]);

         if (scissor) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
         }
      }
   }

   private static void renderTexture(ShaderChannel channel, int texture, int width, int height) {
      GlState state = captureGlState();

      try {
         GL13.glActiveTexture(GL13.GL_TEXTURE0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
         GL11.glDisable(GL11.GL_DEPTH_TEST);
         GL11.glDisable(GL11.GL_CULL_FACE);
         GL11.glDisable(GL11.GL_STENCIL_TEST);
         GL11.glDisable(GL11.GL_SCISSOR_TEST);
         GL11.glColorMask(true, true, true, true);
         configureBlend(channel);
         GL20.glUseProgram(channel.program);
         setUniform1i("colorTex", 0);
         setUniform2f("resolution", width, height);
         setUniform2f("uResolution", width, height);
         float time = (float) (System.currentTimeMillis() / 1000.0D);
         setUniform1f("time", time);
         setUniform1f("uTime", time);
         applyParameters(channel);

         ensureFullscreenQuad();
         GL30.glBindVertexArray(fullscreenVao);
         GL20.glEnableVertexAttribArray(0);
         
         GL20.glEnableVertexAttribArray(8);
         int passes = channel.effective == null ? 1 : Math.max(1, channel.effective.passes);
         for (int pass = 0; pass < passes; ++pass) {
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
         }
      } finally {
         restoreGlState(state);
      }
   }

   /**
    * Draws the painted hand through the alpha of the captured hand layer.
    *
    * <p>The result is composited with a premultiplied "over" blend, so the hand
    * stays opaque and the world behind it is never touched, no matter what alpha
    * the effect itself outputs.</p>
    */
   private static void compositeHand(int colorTexture, int maskTexture, int width, int height) {
      int program = ensureMaskProgram();
      GlState state = captureGlState();

      try {
         GL13.glActiveTexture(GL13.GL_TEXTURE0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
         GL13.glActiveTexture(GL13.GL_TEXTURE1);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, maskTexture);
         GL11.glDisable(GL11.GL_DEPTH_TEST);
         GL11.glDisable(GL11.GL_CULL_FACE);
         GL11.glDisable(GL11.GL_STENCIL_TEST);
         GL11.glDisable(GL11.GL_SCISSOR_TEST);
         GL11.glColorMask(true, true, true, true);
         GL11.glEnable(GL11.GL_BLEND);
         GL20.glBlendEquation(GL20.GL_FUNC_ADD);
         GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
         GL20.glUseProgram(program);
         GL20.glUniform1i(GL20.glGetUniformLocation(program, "colorTex"), 0);
         GL20.glUniform1i(GL20.glGetUniformLocation(program, "maskTex"), 1);

         ensureFullscreenQuad();
         GL30.glBindVertexArray(fullscreenVao);
         GL20.glEnableVertexAttribArray(0);
         GL20.glEnableVertexAttribArray(8);
         GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
      } finally {
         restoreGlState(state);
      }
   }

   private static int ensureMaskProgram() {
      if (maskProgram != 0) {
         return maskProgram;
      }

      int vertexShader = 0;
      int fragmentShader = 0;
      int program = 0;
      try {
         vertexShader = compile(GL20.GL_VERTEX_SHADER, MASK_VERTEX, "Вершинный шейдер маски");
         fragmentShader = compile(GL20.GL_FRAGMENT_SHADER, MASK_FRAGMENT, "Фрагментный шейдер маски");
         program = GL20.glCreateProgram();
         GL20.glAttachShader(program, vertexShader);
         GL20.glAttachShader(program, fragmentShader);
         GL20.glLinkProgram(program);
         if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            throw new IllegalArgumentException("Ошибка связывания:\n" + GL20.glGetProgramInfoLog(program, 8192));
         }
         maskProgram = program;
         return maskProgram;
      } finally {
         if (vertexShader != 0) GL20.glDeleteShader(vertexShader);
         if (fragmentShader != 0) GL20.glDeleteShader(fragmentShader);
         if (program != 0 && maskProgram != program) GL20.glDeleteProgram(program);
      }
   }

   private static void ensureFullscreenQuad() {
      if (fullscreenVao != 0 && fullscreenVbo != 0) {
         return;
      }
      fullscreenVao = GL30.glGenVertexArrays();
      fullscreenVbo = GL15.glGenBuffers();
      GL30.glBindVertexArray(fullscreenVao);
      try {
         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, fullscreenVbo);
         java.nio.FloatBuffer vertices = BufferUtils.createFloatBuffer(16);
         vertices.put(-1.0F).put(-1.0F).put(0.0F).put(0.0F);
         vertices.put(1.0F).put(-1.0F).put(1.0F).put(0.0F);
         vertices.put(-1.0F).put(1.0F).put(0.0F).put(1.0F);
         vertices.put(1.0F).put(1.0F).put(1.0F).put(1.0F);
         vertices.flip();
         GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
         GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 16, 0L);
         GL20.glVertexAttribPointer(8, 2, GL11.GL_FLOAT, false, 16, 8L);
      } finally {
         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
         GL30.glBindVertexArray(0);
      }
   }

   private static void configureBlend(ShaderChannel channel) {
      /* The hand channel holds the whole first person frame, so it always replaces the screen, otherwise the effect's additive mode makes the hand translucent */
      if (channel == hand) {
         GL11.glDisable(GL11.GL_BLEND);

         return;
      }

      String mode = channel.effective == null ? null : channel.effective.blendMode;
      String value = mode == null ? "replace" : mode.trim().toLowerCase();
      if ("additive".equals(value)) {
         GL11.glEnable(GL11.GL_BLEND);
         GL20.glBlendEquation(GL20.GL_FUNC_ADD);
         GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
      } else if ("alpha".equals(value)) {
         GL11.glEnable(GL11.GL_BLEND);
         GL20.glBlendEquation(GL20.GL_FUNC_ADD);
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      } else {
         GL11.glDisable(GL11.GL_BLEND);
      }
   }

   private static Map<String, Integer> collectUniformTypes(int program) {
      Map<String, Integer> types = new HashMap<String, Integer>();
      IntBuffer size = BufferUtils.createIntBuffer(1);
      IntBuffer type = BufferUtils.createIntBuffer(1);
      int count = GL20.glGetProgrami(program, GL20.GL_ACTIVE_UNIFORMS);

      for (int i = 0; i < count; ++i) {
         size.clear();
         type.clear();
         String name = GL20.glGetActiveUniform(program, i, size, type);

         if (name == null || name.isEmpty()) {
            continue;
         }

         int uniformType = type.get(0);
         types.put(name, uniformType);

         int bracket = name.indexOf('[');
         if (bracket > 0) {
            types.put(name.substring(0, bracket), uniformType);
         }
      }

      return types;
   }

   private static void applyParameters(ShaderChannel channel) {
      ShaderFile shader = channel.effective;

      if (shader == null || shader.parameters == null || shader.parameters.isEmpty() || channel.program == 0) {
         return;
      }

      for (Map.Entry<String, Object> entry : shader.parameters.entrySet()) {
         String key = entry.getKey();

         if (key == null || ShaderParameters.isConfigEntry(key)) {
            continue;
         }

         String name = ShaderParameters.uniformName(key);

         if (name.isEmpty() || "colorTex".equals(name) || "maskTex".equals(name)) {
            continue;
         }

         Integer type = channel.uniformTypes.get(name);

         if (type == null) {
            continue;
         }

         int location = GL20.glGetUniformLocation(channel.program, name);

         if (location < 0) {
            continue;
         }

         setUniform(location, type, entry.getValue());
      }
   }

   private static void setUniform(int location, int type, Object value) {
      switch (type) {
         case GL20.GL_FLOAT:
            GL20.glUniform1f(location, toFloat(value));
            break;
         case GL20.GL_FLOAT_VEC2: {
            float[] values = toFloatArray(value, 2);
            GL20.glUniform2f(location, values[0], values[1]);
            break;
         }
         case GL20.GL_FLOAT_VEC3: {
            float[] values = toFloatArray(value, 3);
            GL20.glUniform3f(location, values[0], values[1], values[2]);
            break;
         }
         case GL20.GL_FLOAT_VEC4: {
            float[] values = toFloatArray(value, 4);
            GL20.glUniform4f(location, values[0], values[1], values[2], values[3]);
            break;
         }
         case GL20.GL_INT:
         case GL20.GL_BOOL:
            GL20.glUniform1i(location, toInt(value));
            break;
         case GL20.GL_INT_VEC2:
         case GL20.GL_BOOL_VEC2: {
            int[] values = toIntArray(value, 2);
            GL20.glUniform2i(location, values[0], values[1]);
            break;
         }
         case GL20.GL_INT_VEC3:
         case GL20.GL_BOOL_VEC3: {
            int[] values = toIntArray(value, 3);
            GL20.glUniform3i(location, values[0], values[1], values[2]);
            break;
         }
         case GL20.GL_INT_VEC4:
         case GL20.GL_BOOL_VEC4: {
            int[] values = toIntArray(value, 4);
            GL20.glUniform4i(location, values[0], values[1], values[2], values[3]);
            break;
         }
         case GL20.GL_FLOAT_MAT2:
            GL20.glUniformMatrix2fv(location, false, toFloatArray(value, 4));
            break;
         case GL20.GL_FLOAT_MAT3:
            GL20.glUniformMatrix3fv(location, false, toFloatArray(value, 9));
            break;
         case GL20.GL_FLOAT_MAT4:
            GL20.glUniformMatrix4fv(location, false, toFloatArray(value, 16));
            break;
         default:
            break;
      }
   }

   private static float toFloat(Object value) {
      if (value instanceof Number) {
         return ((Number) value).floatValue();
      }

      if (value instanceof Boolean) {
         return (Boolean) value ? 1.0F : 0.0F;
      }

      try {
         return Float.parseFloat(String.valueOf(value));
      } catch (NumberFormatException ignored) {
         return 0.0F;
      }
   }

   private static int toInt(Object value) {
      if (value instanceof Number) {
         return ((Number) value).intValue();
      }

      if (value instanceof Boolean) {
         return (Boolean) value ? 1 : 0;
      }

      try {
         return Integer.parseInt(String.valueOf(value).trim());
      } catch (NumberFormatException ignored) {
         return 0;
      }
   }

   private static float[] toFloatArray(Object value, int length) {
      float[] result = new float[length];
      List<Float> values = new ArrayList<Float>();
      flatten(value, values);

      for (int i = 0; i < length && i < values.size(); ++i) {
         result[i] = values.get(i);
      }

      return result;
   }

   private static int[] toIntArray(Object value, int length) {
      int[] result = new int[length];
      List<Float> values = new ArrayList<Float>();
      flatten(value, values);

      for (int i = 0; i < length && i < values.size(); ++i) {
         result[i] = values.get(i).intValue();
      }

      return result;
   }

   private static void flatten(Object value, List<Float> out) {
      if (value == null) {
         return;
      }

      if (value instanceof Number) {
         out.add(((Number) value).floatValue());

         return;
      }

      if (value instanceof Boolean) {
         out.add((Boolean) value ? 1.0F : 0.0F);

         return;
      }

      if (value instanceof List) {
         for (Object element : (List<?>) value) {
            flatten(element, out);
         }

         return;
      }

      if (value.getClass().isArray()) {
         int length = Array.getLength(value);

         for (int i = 0; i < length; ++i) {
            flatten(Array.get(value, i), out);
         }

         return;
      }

      try {
         out.add(Float.parseFloat(String.valueOf(value).trim()));
      } catch (NumberFormatException ignored) {
         /* Not a number, skip it. */
      }
   }

   private static void setUniform1i(String name, int value) {
      int location = GL20.glGetUniformLocation(getCurrentProgram(), name);
      if (location >= 0) GL20.glUniform1i(location, value);
   }

   private static void setUniform1f(String name, float value) {
      int location = GL20.glGetUniformLocation(getCurrentProgram(), name);
      if (location >= 0) GL20.glUniform1f(location, value);
   }

   private static void setUniform2f(String name, float x, float y) {
      int location = GL20.glGetUniformLocation(getCurrentProgram(), name);
      if (location >= 0) GL20.glUniform2f(location, x, y);
   }

   private static int getCurrentProgram() {
      return GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
   }

   private static GlState captureGlState() {
      GlState state = new GlState();

      state.program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
      state.vao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
      state.arrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
      state.activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      state.texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
      GL13.glActiveTexture(GL13.GL_TEXTURE1);
      state.texture1 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
      GL13.glActiveTexture(state.activeTexture);

      state.depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
      state.cullFace = GL11.glIsEnabled(GL11.GL_CULL_FACE);
      state.blend = GL11.glIsEnabled(GL11.GL_BLEND);
      state.stencilTest = GL11.glIsEnabled(GL11.GL_STENCIL_TEST);
      state.scissorTest = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);

      state.depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
      getColorMask(state.colorMask);

      state.blendSrcRgb = GL11.glGetInteger(GL11.GL_BLEND_SRC);
      state.blendDstRgb = GL11.glGetInteger(GL11.GL_BLEND_DST);
      state.blendSrcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
      state.blendDstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
      state.blendEquationRgb = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
      state.blendEquationAlpha = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);

      state.cullFaceMode = GL11.glGetInteger(GL11.GL_CULL_FACE_MODE);
      state.frontFace = GL11.glGetInteger(GL11.GL_FRONT_FACE);

      state.stencilFrontFunc = GL11.glGetInteger(GL11.GL_STENCIL_FUNC);
      state.stencilFrontRef = GL11.glGetInteger(GL11.GL_STENCIL_REF);
      state.stencilFrontValueMask = GL11.glGetInteger(GL11.GL_STENCIL_VALUE_MASK);
      state.stencilFrontWriteMask = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
      state.stencilFrontFail = GL11.glGetInteger(GL11.GL_STENCIL_FAIL);
      state.stencilFrontDepthFail = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_FAIL);
      state.stencilFrontDepthPass = GL11.glGetInteger(GL11.GL_STENCIL_PASS_DEPTH_PASS);

      state.stencilBackFunc = GL11.glGetInteger(GL20.GL_STENCIL_BACK_FUNC);
      state.stencilBackRef = GL11.glGetInteger(GL20.GL_STENCIL_BACK_REF);
      state.stencilBackValueMask = GL11.glGetInteger(GL20.GL_STENCIL_BACK_VALUE_MASK);
      state.stencilBackWriteMask = GL11.glGetInteger(GL20.GL_STENCIL_BACK_WRITEMASK);
      state.stencilBackFail = GL11.glGetInteger(GL20.GL_STENCIL_BACK_FAIL);
      state.stencilBackDepthFail = GL11.glGetInteger(GL20.GL_STENCIL_BACK_PASS_DEPTH_FAIL);
      state.stencilBackDepthPass = GL11.glGetInteger(GL20.GL_STENCIL_BACK_PASS_DEPTH_PASS);

      GL11.glGetIntegerv(GL11.GL_VIEWPORT, state.viewport);
      GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, state.scissorBox);
      GL11.glGetFloatv(GL11.GL_COLOR_CLEAR_VALUE, state.clearColor);

      return state;
   }

   private static void restoreGlState(GlState state) {
      GL20.glUseProgram(state.program);
      GL30.glBindVertexArray(state.vao);
      GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, state.arrayBuffer);

      GL13.glActiveTexture(GL13.GL_TEXTURE1);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, state.texture1);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, state.texture0);
      GL13.glActiveTexture(state.activeTexture);

      GL11.glViewport(state.viewport[0], state.viewport[1], state.viewport[2], state.viewport[3]);
      GL11.glScissor(state.scissorBox[0], state.scissorBox[1], state.scissorBox[2], state.scissorBox[3]);

      GL11.glColorMask(state.colorMask[0], state.colorMask[1], state.colorMask[2], state.colorMask[3]);
      GL11.glDepthMask(state.depthMask);
      GL14.glBlendFuncSeparate(state.blendSrcRgb, state.blendDstRgb, state.blendSrcAlpha, state.blendDstAlpha);
      GL20.glBlendEquationSeparate(state.blendEquationRgb, state.blendEquationAlpha);
      GL11.glCullFace(state.cullFaceMode);
      GL11.glFrontFace(state.frontFace);
      GL11.glClearColor(state.clearColor[0], state.clearColor[1], state.clearColor[2], state.clearColor[3]);

      GL20.glStencilFuncSeparate(GL11.GL_FRONT, state.stencilFrontFunc, state.stencilFrontRef, state.stencilFrontValueMask);
      GL20.glStencilFuncSeparate(GL11.GL_BACK, state.stencilBackFunc, state.stencilBackRef, state.stencilBackValueMask);
      GL20.glStencilOpSeparate(GL11.GL_FRONT, state.stencilFrontFail, state.stencilFrontDepthFail, state.stencilFrontDepthPass);
      GL20.glStencilOpSeparate(GL11.GL_BACK, state.stencilBackFail, state.stencilBackDepthFail, state.stencilBackDepthPass);
      GL20.glStencilMaskSeparate(GL11.GL_FRONT, state.stencilFrontWriteMask);
      GL20.glStencilMaskSeparate(GL11.GL_BACK, state.stencilBackWriteMask);

      if (state.depthTest) GL11.glEnable(GL11.GL_DEPTH_TEST); else GL11.glDisable(GL11.GL_DEPTH_TEST);
      if (state.cullFace) GL11.glEnable(GL11.GL_CULL_FACE); else GL11.glDisable(GL11.GL_CULL_FACE);
      if (state.blend) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
      if (state.stencilTest) GL11.glEnable(GL11.GL_STENCIL_TEST); else GL11.glDisable(GL11.GL_STENCIL_TEST);
      if (state.scissorTest) GL11.glEnable(GL11.GL_SCISSOR_TEST); else GL11.glDisable(GL11.GL_SCISSOR_TEST);
   }

   private static void getColorMask(boolean[] mask) {
      ByteBuffer buffer = BufferUtils.createByteBuffer(4);
      GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, buffer);

      for (int i = 0; i < 4; ++i) {
         mask[i] = buffer.get(i) != 0;
      }
   }

   private static void handleRenderFailure(ShaderChannel channel, Throwable throwable) {
      String message = throwable.getMessage();
      setError("Рендер шейдера остановлен: " + (message == null || message.isEmpty() ? throwable.getClass().getSimpleName() : message));
      channel.capturing = false;
      removeChannelAfterFailure(channel);
   }

   private static void handleCaptureFailure(ShaderChannel capture, Throwable throwable) {
      String message = throwable.getMessage();
      setError("Захват слоя шейдера остановлен: " + (message == null || message.isEmpty() ? throwable.getClass().getSimpleName() : message));
      capture.capturing = false;
      if (capture == hand) {
         removeChannelAfterFailure(capture);
         deleteCaptureBuffer(capture);
      } else {
         removeChannelAfterFailure(capture);
      }
   }

   private static void removeChannelAfterFailure(ShaderChannel channel) {
      runOnRenderThread(() -> {
         channel.active = null;
         channel.effective = null;
         channel.activeName = "";
         channel.uniformTypes = new HashMap<String, Integer>();

         if (channel.program != 0) {
            GL20.glDeleteProgram(channel.program);
            channel.program = 0;
         }
      });
   }

   private static ShaderChannel getChannel(int target) {
      if (target == TARGET_INTERFACE) {
         return interfaceChannel;
      }
      return target == TARGET_HUD ? hud : screen;
   }

   private static void setError(String message) {
      error = message == null ? "Неизвестная ошибка шейдера" : message;
   }

   public static void reset() {
      runOnRenderThread(() -> {
         removeChannel(screen);
         removeChannel(interfaceChannel);
         removeChannel(hud);
         removeChannel(hand);

         if (copiedScreenTexture != 0) {
            GL11.glDeleteTextures(copiedScreenTexture);
            copiedScreenTexture = 0;
         }

         deleteCaptureBuffer(interfaceChannel);
         deleteCaptureBuffer(hud);
         deleteCaptureBuffer(hand);
         deleteCaptureBuffer(handScratch);

         if (maskProgram != 0) {
            GL20.glDeleteProgram(maskProgram);
            maskProgram = 0;
         }

         if (fullscreenVbo != 0) {
            GL15.glDeleteBuffers(fullscreenVbo);
            fullscreenVbo = 0;
         }

         if (fullscreenVao != 0) {
            GL30.glDeleteVertexArrays(fullscreenVao);
            fullscreenVao = 0;
         }
      });
   }

   private static void deleteCaptureBuffer(ShaderChannel channel) {
      runOnRenderThread(() -> {
         channel.capturing = false;
         channel.width = 0;
         channel.height = 0;

         if (channel.texture != 0) {
            GL11.glDeleteTextures(channel.texture);
            channel.texture = 0;
         }

         if (channel.depthStencilBuffer != 0) {
            GL30.glDeleteRenderbuffers(channel.depthStencilBuffer);
            channel.depthStencilBuffer = 0;
         }

         if (channel.framebuffer != 0) {
            GL30.glDeleteFramebuffers(channel.framebuffer);
            channel.framebuffer = 0;
         }
      });
   }

   private static class ShaderChannel {
      public final int target;
      public final boolean stencil;
      public int program;

      public ShaderChannel(int target, boolean stencil) {
         this.target = target;
         this.stencil = stencil;
      }
      public ShaderFile active;
      public ShaderFile effective;
      public String activeName = "";
      public Map<String, Integer> uniformTypes = new HashMap<String, Integer>();
      public int texture;
      public int framebuffer;
      public int depthStencilBuffer;
      public int width;
      public int height;
      public int previousDrawFramebuffer;
      public int previousReadFramebuffer;
      public int[] previousViewport = new int[4];
      public boolean capturing;
   }

   private static class GlState {
      public int program;
      public int vao;
      public int arrayBuffer;
      public int activeTexture;
      public int texture0;
      public int texture1;
      public boolean depthTest;
      public boolean cullFace;
      public boolean blend;
      public boolean stencilTest;
      public boolean scissorTest;
      public boolean depthMask;
      public boolean[] colorMask = new boolean[4];
      public int blendSrcRgb;
      public int blendDstRgb;
      public int blendSrcAlpha;
      public int blendDstAlpha;
      public int blendEquationRgb;
      public int blendEquationAlpha;
      public int cullFaceMode;
      public int frontFace;
      public int stencilFrontFunc;
      public int stencilFrontRef;
      public int stencilFrontValueMask;
      public int stencilFrontWriteMask;
      public int stencilFrontFail;
      public int stencilFrontDepthFail;
      public int stencilFrontDepthPass;
      public int stencilBackFunc;
      public int stencilBackRef;
      public int stencilBackValueMask;
      public int stencilBackWriteMask;
      public int stencilBackFail;
      public int stencilBackDepthFail;
      public int stencilBackDepthPass;
      public int[] viewport = new int[4];
      public int[] scissorBox = new int[4];
      public float[] clearColor = new float[4];
   }
}
