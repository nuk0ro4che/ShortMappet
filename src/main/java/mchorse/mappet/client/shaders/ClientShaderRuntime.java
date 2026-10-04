package mchorse.mappet.client.shaders;

import mchorse.mappet.api.shaders.ShaderFile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;






@Environment(EnvType.CLIENT)
public final class ClientShaderRuntime {
   public static final int TARGET_SCREEN = 0;
   public static final int TARGET_INTERFACE = 1;
   public static final int TARGET_HUD = 2;
   private static final int TARGET_HAND = 3;

   private static final ShaderChannel screen = new ShaderChannel(TARGET_SCREEN);
   private static final ShaderChannel interfaceChannel = new ShaderChannel(TARGET_INTERFACE);
   private static final ShaderChannel hud = new ShaderChannel(TARGET_HUD);
   
   private static final ShaderChannel hand = new ShaderChannel(TARGET_HAND);
   private static final ShaderChannel handScratch = new ShaderChannel(TARGET_HAND);
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
      return apply(shader, TARGET_SCREEN);
   }

   
   public static boolean applyInterface(ShaderFile shader) {
      return apply(shader, TARGET_INTERFACE);
   }

   
   public static boolean applyHud(ShaderFile shader) {
      return apply(shader, TARGET_HUD);
   }

   public static boolean apply(ShaderFile shader, int target) {
      if (shader == null) {
         setError("Шейдер не выбран");
         return false;
      }

      boolean[] applied = new boolean[]{false};

      runOnRenderThread(() -> {
         if (!GL.getCapabilities().OpenGL20 || !GL.getCapabilities().OpenGL30) {
            setError("Видеодрайвер не поддерживает современный OpenGL, нужный для экранного шейдера");
            return;
         }

         applied[0] = applyToChannel(shader, getChannel(target));

         if (target == TARGET_SCREEN) {
            if (shader.renderOnHand && applied[0]) {
               applyToChannel(shader, hand);
            } else if (!shader.renderOnHand) {
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

   private static boolean applyToChannel(ShaderFile shader, ShaderChannel channel) {
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
         channel.active = shader;
         channel.activeName = shader.getId();
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
         channel.activeName = "";
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
         ensureCopiedScreenTexture();
         GL13.glActiveTexture(GL13.GL_TEXTURE0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, copiedScreenTexture);
         GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 0, 0, width, height, 0);
         renderTexture(screen, copiedScreenTexture, width, height);
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
      if (hand.active != null && hand.active.renderOnHand) {
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
      return channel.program != 0 && channel.active != null && channel.active.enabled && (!honorWorldFlag || channel.active.world) && !isHiddenByF1(channel);
   }

   
   private static boolean isHiddenByF1(ShaderChannel channel) {
      return channel.active != null && channel.active.hideInFirstPerson && class_310.method_1551().field_1690.field_1842;
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
         ensureCaptureBuffer(capture, width, height);
         capture.previousFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
         GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, capture.framebuffer);
         GL11.glViewport(0, 0, width, height);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
         capture.capturing = true;
      } catch (Throwable throwable) {
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
         GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, capture.previousFramebuffer);
         GL11.glViewport(0, 0, width, height);

         if (masked) {
            /* The effect paints the whole frame, so it is applied to the hand layer first and then composited through the captured hand alpha, otherwise the effect's own alpha (usually 1.0) erases the world */
            ensureCaptureBuffer(handScratch, width, height);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, handScratch.framebuffer);
            renderTexture(effect, capture.texture, width, height);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, capture.previousFramebuffer);
            GL11.glViewport(0, 0, width, height);
            compositeHand(handScratch.texture, capture.texture, width, height);
         } else {
            renderTexture(effect, capture.texture, width, height);
         }
      } catch (Throwable throwable) {
         handleRenderFailure(effect, throwable);
      } finally {
         capture.previousFramebuffer = 0;
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
         channel.depthBuffer = GL30.glGenRenderbuffers();
      }
      if (channel.width == width && channel.height == height) {
         return;
      }

      int previousFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
      int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
      int previousRenderbuffer = GL11.glGetInteger(GL30.GL_RENDERBUFFER_BINDING);
      try {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, channel.texture);
         configureTexture();
         GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0L);
         GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, channel.framebuffer);
         GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, channel.texture, 0);
         GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, channel.depthBuffer);
         GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT24, width, height);
         GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, channel.depthBuffer);
         if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Не удалось создать буфер для UI/HUD шейдера");
         }
         channel.width = width;
         channel.height = height;
      } finally {
         GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, previousRenderbuffer);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
         GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previousFramebuffer);
      }
   }

   private static void configureTexture() {
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
   }

   private static void renderTexture(ShaderChannel channel, int texture, int width, int height) {
      int previousProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
      int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
      boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
      boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
      boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
      int blendSource = GL11.glGetInteger(GL11.GL_BLEND_SRC);
      int blendDestination = GL11.glGetInteger(GL11.GL_BLEND_DST);
      int previousVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
      int previousArrayBuffer = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
      try {
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
         GL11.glDisable(GL11.GL_DEPTH_TEST);
         GL11.glDisable(GL11.GL_CULL_FACE);
         configureBlend(channel);
         GL20.glUseProgram(channel.program);
         setUniform1i("colorTex", 0);
         setUniform2f("resolution", width, height);
         setUniform2f("uResolution", width, height);
         setUniform1f("time", (float)(System.currentTimeMillis() / 1000.0D));
         setUniform1f("uTime", (float)(System.currentTimeMillis() / 1000.0D));

         ensureFullscreenQuad();
         GL30.glBindVertexArray(fullscreenVao);
         GL20.glEnableVertexAttribArray(0);
         
         GL20.glEnableVertexAttribArray(8);
         for (int pass = 0; pass < Math.max(1, channel.active.passes); ++pass) {
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
         }
      } finally {
         GL30.glBindVertexArray(previousVao);
         GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, previousArrayBuffer);
         GL20.glUseProgram(previousProgram);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);
         GL13.glActiveTexture(previousActiveTexture);
         if (depth) GL11.glEnable(GL11.GL_DEPTH_TEST); else GL11.glDisable(GL11.GL_DEPTH_TEST);
         if (cull) GL11.glEnable(GL11.GL_CULL_FACE); else GL11.glDisable(GL11.GL_CULL_FACE);
         GL11.glBlendFunc(blendSource, blendDestination);
         if (blend) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
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
      int previousProgram = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
      int previousActiveTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
      boolean depth = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
      boolean cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
      boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
      int blendSource = GL11.glGetInteger(GL11.GL_BLEND_SRC);
      int blendDestination = GL11.glGetInteger(GL11.GL_BLEND_DST);
      int previousVao = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
      GL13.glActiveTexture(GL13.GL_TEXTURE0);
      int previousTexture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
      GL13.glActiveTexture(GL13.GL_TEXTURE1);
      int previousTexture1 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
      try {
         GL13.glActiveTexture(GL13.GL_TEXTURE0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
         GL13.glActiveTexture(GL13.GL_TEXTURE1);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, maskTexture);
         GL11.glDisable(GL11.GL_DEPTH_TEST);
         GL11.glDisable(GL11.GL_CULL_FACE);
         GL11.glEnable(GL11.GL_BLEND);
         GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
         GL20.glUseProgram(program);
         GL20.glUniform1i(GL20.glGetUniformLocation(program, "colorTex"), 0);
         GL20.glUniform1i(GL20.glGetUniformLocation(program, "maskTex"), 1);

         ensureFullscreenQuad();
         GL30.glBindVertexArray(fullscreenVao);
         GL20.glEnableVertexAttribArray(0);
         GL20.glEnableVertexAttribArray(8);
         GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
         GL30.glBindVertexArray(0);
      } finally {
         GL13.glActiveTexture(GL13.GL_TEXTURE1);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture1);
         GL13.glActiveTexture(GL13.GL_TEXTURE0);
         GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture0);
         GL30.glBindVertexArray(previousVao);
         GL20.glUseProgram(previousProgram);
         GL13.glActiveTexture(previousActiveTexture);
         if (depth) GL11.glEnable(GL11.GL_DEPTH_TEST); else GL11.glDisable(GL11.GL_DEPTH_TEST);
         if (cull) GL11.glEnable(GL11.GL_CULL_FACE); else GL11.glDisable(GL11.GL_CULL_FACE);
         GL11.glBlendFunc(blendSource, blendDestination);
         if (blend) GL11.glEnable(GL11.GL_BLEND); else GL11.glDisable(GL11.GL_BLEND);
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

      String mode = channel.active.blendMode;
      String value = mode == null ? "replace" : mode.trim().toLowerCase();
      if ("additive".equals(value)) {
         GL11.glEnable(GL11.GL_BLEND);
         GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
      } else if ("alpha".equals(value)) {
         GL11.glEnable(GL11.GL_BLEND);
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      } else {
         GL11.glDisable(GL11.GL_BLEND);
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
         channel.activeName = "";

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

         if (channel.depthBuffer != 0) {
            GL30.glDeleteRenderbuffers(channel.depthBuffer);
            channel.depthBuffer = 0;
         }

         if (channel.framebuffer != 0) {
            GL30.glDeleteFramebuffers(channel.framebuffer);
            channel.framebuffer = 0;
         }
      });
   }

   private static class ShaderChannel {
      public final int target;
      public int program;

      public ShaderChannel(int target) {
         this.target = target;
      }
      public ShaderFile active;
      public String activeName = "";
      public int texture;
      public int framebuffer;
      public int depthBuffer;
      public int width;
      public int height;
      public int previousFramebuffer;
      public boolean capturing;
   }
}
