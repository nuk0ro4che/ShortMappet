package mchorse.mappet.client.gui.panels;

import com.google.common.collect.ImmutableSet;
import java.util.regex.Pattern;
import mchorse.mappet.api.shaders.ShaderFile;
import mchorse.mappet.api.utils.ContentType;
import mchorse.mappet.client.gui.GuiMappetDashboard;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.shaders.ClientShaderRuntime;
import mchorse.mappet.client.gui.scripts.utils.SyntaxHighlighter;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTrackpadElement;
import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.mclib.client.gui.framework.elements.utils.GuiDraw;
import mchorse.mclib.client.gui.framework.elements.utils.GuiLabel;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.mclib.utils.Direction;
import net.minecraft.class_310;


public class GuiShaderPanel extends GuiMappetDashboardPanel<ShaderFile> {
   private static final float VERTEX_WIDTH = 0.36F;
   private final GuiElement settings;
   private final GuiElement codeArea;
   private final GuiLabel vertexLabel;
   private final GuiLabel fragmentLabel;
   private final GuiTextEditor vertex;
   private final GuiTextEditor fragment;
   private final GuiToggleElement enabled;
   private final GuiToggleElement global;
   private final GuiToggleElement depthBuffer;
   private final GuiToggleElement world;
   private final GuiToggleElement hideInFirstPerson;
   private final GuiToggleElement renderOnHand;
   private final GuiTrackpadElement renderStage;
   private final GuiButtonElement blendMode;
   private static final String[] BLEND_MODES = {"additive", "alpha", "multiply", "screen"};
   private final GuiTrackpadElement passes;
   private final GuiTrackpadElement scale;
   private final GuiTrackpadElement duration;
   private final GuiToggleElement shadowMap;
   private final GuiTrackpadElement shadowResolution;
   private final GuiTrackpadElement shadowDistance;
   private final GuiIconElement apply;
   private final GuiIconElement removeShader;
   private boolean filling;
   private String runtimeStatus = "";

   public GuiShaderPanel(class_310 mc, GuiMappetDashboard dashboard) {
      super(mc, dashboard);
      this.namesList.setFileIcon(Icons.MATERIAL);
      this.apply = new GuiIconElement(mc, Icons.PLAY, (button) -> this.setApplied(true));
      this.apply.tooltip(IKey.str("Применить шейдер"), Direction.RIGHT);
      this.removeShader = new GuiIconElement(mc, Icons.REMOVE, (button) -> this.setApplied(false));
      this.removeShader.tooltip(IKey.str("Снять шейдер"), Direction.RIGHT);
      this.iconBar.add(new IGuiElement[]{this.apply, this.removeShader});
      this.settings = this.createScrollEditor();
      this.codeArea = new GuiElement(mc);
      this.vertexLabel = new GuiLabel(mc, IKey.str("Вершинный шейдер")).anchor(0.5F, 0.5F).background(0xC0222228);
      this.fragmentLabel = new GuiLabel(mc, IKey.str("Фрагментный шейдер")).anchor(0.5F, 0.5F).background(0xC0222228);
      this.vertex = new GuiTextEditor(mc, (value) -> {
         if (this.data != null && !this.filling) {
            this.data.vertex = value;
         }
      });
      this.fragment = new GuiTextEditor(mc, (value) -> {
         if (this.data != null && !this.filling) {
            this.data.fragment = value;
         }
      });
      SyntaxHighlighter glsl = this.createGlslHighlighter();
      this.vertex.setHighlighter(glsl);
      this.fragment.setHighlighter(glsl);
      this.vertex.background();
      this.fragment.background();
      this.enabled = this.toggle(mc, "Включено", (value) -> this.data.enabled = value);
      this.global = this.toggle(mc, "Глобально", (value) -> this.data.global = value);
      this.depthBuffer = this.toggle(mc, "Буфер глубины", (value) -> this.data.depthBuffer = value);
      this.world = this.toggle(mc, "Мир", (value) -> this.data.world = value);
      this.hideInFirstPerson = this.toggle(mc, "Прятать в F1", (value) -> this.data.hideInFirstPerson = value);
      this.hideInFirstPerson.tooltip(IKey.str("Не рисовать этот шейдер, когда игрок скрыл интерфейс клавишей F1"), Direction.RIGHT);
      this.renderOnHand = this.toggle(mc, "Рендер на руку", (value) -> this.data.renderOnHand = value);
      this.renderOnHand.tooltip(IKey.str("Пропустить шейдер через руку и предметы от первого лица"), Direction.RIGHT);
      this.renderStage = this.number(mc, (value) -> this.data.renderStage = value.intValue()).limit(0.0D, 16.0D, true);
      this.blendMode = new GuiButtonElement(mc, IKey.str("Режим смешивания: additive"), (button) -> {
         if (this.data == null || this.filling) return;
         int current = 0;
         for (int i = 0; i < BLEND_MODES.length; ++i) {
            if (BLEND_MODES[i].equalsIgnoreCase(this.data.blendMode)) {
               current = i;
               break;
            }
         }
         this.data.blendMode = BLEND_MODES[(current + 1) % BLEND_MODES.length];
         button.label = IKey.str("Режим смешивания: " + this.data.blendMode);
      }).background(true);
      this.passes = this.number(mc, (value) -> this.data.passes = Math.max(1, value.intValue())).limit(1.0D, 32.0D, true);
      this.scale = this.number(mc, (value) -> this.data.scale = Math.max(0.05F, value.floatValue())).limit(0.05D, 2.0D);
      this.duration = this.number(mc, (value) -> this.data.duration = Math.max(0, value.intValue())).limit(0.0D, 120000.0D, true);
      this.shadowMap = this.toggle(mc, "Карта теней", (value) -> this.data.shadowMap = value);
      this.shadowResolution = this.number(mc, (value) -> this.data.shadowResolution = Math.max(16, value.intValue())).limit(16.0D, 8192.0D, true);
      this.shadowDistance = this.number(mc, (value) -> this.data.shadowDistance = Math.max(1, value.intValue())).limit(1.0D, 1024.0D, true);

      this.settings.add(new IGuiElement[]{
         Elements.label(IKey.str("Настройки")),
         this.enabled, this.global, this.depthBuffer,
         Elements.label(IKey.str("Стадия рендера")).marginTop(8), this.renderStage,
         this.world, this.hideInFirstPerson, this.renderOnHand,
         Elements.label(IKey.str("Режим смешивания")).marginTop(8), this.blendMode,
         Elements.label(IKey.str("Кол-во проходов")), this.passes,
         Elements.label(IKey.str("Масштаб рендера")), this.scale,
         Elements.label(IKey.str("Длительность (тики, 0 — бесконечно)")), this.duration,
         Elements.label(IKey.str("Тени (WIP)")).marginTop(8), this.shadowMap,
         Elements.label(IKey.str("Разрешение теней")), this.shadowResolution,
         Elements.label(IKey.str("Дальность теней")), this.shadowDistance
      });

      this.settings.flex().relative(this.editor).w(220).h(1.0F);
      this.codeArea.flex().relative(this.editor).x(225).w(1.0F, -225).h(1.0F);
      this.vertexLabel.flex().relative(this.codeArea).w(VERTEX_WIDTH, -2).h(20);
      this.fragmentLabel.flex().relative(this.codeArea).x(VERTEX_WIDTH, 2).w(1.0F - VERTEX_WIDTH, -2).h(20);
      this.vertex.flex().relative(this.codeArea).y(22).w(VERTEX_WIDTH, -2).h(1.0F, -22);
      this.fragment.flex().relative(this.codeArea).x(VERTEX_WIDTH, 2).y(22).w(1.0F - VERTEX_WIDTH, -2).h(1.0F, -22);
      this.codeArea.add(new IGuiElement[]{this.vertexLabel, this.fragmentLabel, this.vertex, this.fragment});
      this.editor.add(new IGuiElement[]{this.settings, this.codeArea});
      this.fill(null);
   }

   private void setApplied(boolean applied) {
      if (this.data == null || !this.editor.isEnabled()) {
         return;
      }
      boolean success = applied ? ClientShaderRuntime.apply(this.data) : true;
      if (!applied) {
         ClientShaderRuntime.remove();
      }
      this.data.enabled = applied && success;
      this.enabled.toggled(this.data.enabled);
      this.runtimeStatus = ClientShaderRuntime.getStatus(this.data);
      this.save();
   }

   private GuiToggleElement toggle(class_310 mc, String label, java.util.function.Consumer<Boolean> callback) {
      return new GuiToggleElement(mc, IKey.str(label), (element) -> {
         if (this.data != null && !this.filling) {
            callback.accept(element.isToggled());
         }
      });
   }

   private GuiTrackpadElement number(class_310 mc, java.util.function.Consumer<Double> callback) {
      return new GuiTrackpadElement(mc, (value) -> {
         if (this.data != null && !this.filling) {
            callback.accept(value);
         }
      });
   }

   private SyntaxHighlighter createGlslHighlighter() {
      SyntaxHighlighter highlighter = new SyntaxHighlighter();
      highlighter.operators = ImmutableSet.of("+", "-", "=", "/", "*", "<", ">", "~", "&", "|", "!", "%", "^", "?");
      highlighter.primaryKeywords = ImmutableSet.of("if", "else", "for", "while", "do", "break", "continue", "return", "discard", "switch", "case", "default");
      highlighter.secondaryKeywords = ImmutableSet.of("attribute", "const", "uniform", "varying", "in", "out", "inout", "layout", "precision", "struct", "void", "gl_Position", "gl_FragColor", "gl_FragCoord", "gl_Vertex", "gl_MultiTexCoord0");
      highlighter.special = ImmutableSet.of("main", "texture2D", "texture", "normalize", "dot", "clamp", "mix", "sin", "cos", "pow", "min", "max", "abs", "length");
      highlighter.typeKeywords = ImmutableSet.of("bool", "int", "float", "double", "vec2", "vec3", "vec4", "mat2", "mat3", "mat4", "sampler2D", "samplerCube", "true", "false");
      highlighter.functionName = Pattern.compile("[\\w_][\\d\\w_]*", Pattern.CASE_INSENSITIVE);
      return highlighter;
   }

   public boolean needsBackground() {
      return false;
   }

   public ContentType getType() {
      return ContentType.SHADERS;
   }

   public String getTitle() {
      return "mappet.gui.panels.shaders";
   }

   public void draw(GuiContext context) {
      if (this.data != null && this.editor.isVisible()) {
         this.settings.area.draw(0xC816161B);
         this.codeArea.area.draw(0xE01D1D1D);
         int divider = this.codeArea.area.x + Math.round(this.codeArea.area.w * VERTEX_WIDTH);
         GuiDraw.drawRect(divider - 1, this.codeArea.area.y, divider + 1, this.codeArea.area.ey(), 0xAA111116);
      }
      super.draw(context);
      if (this.data != null && !this.runtimeStatus.isEmpty()) {
         int color = ClientShaderRuntime.hasError() ? 0xFFFF6666 : 0xFF9CFF9C;
         GuiDraw.drawTextBackground(this.font, this.runtimeStatus.replace('\n', ' '), this.codeArea.area.x + 8, this.codeArea.area.ey() - 18, color, 0xDD101015, 3);
      }
   }

   public void fill(ShaderFile shader, boolean allowed) {
      super.fill(shader, allowed);
      boolean visible = shader != null;
      this.settings.setVisible(visible);
      this.codeArea.setVisible(visible);
      this.apply.setEnabled(visible && allowed);
      this.removeShader.setEnabled(visible && allowed);
      if (shader == null) {
         return;
      }

      this.filling = true;
      try {
         this.enabled.toggled(shader.enabled);
         this.global.toggled(shader.global);
         this.depthBuffer.toggled(shader.depthBuffer);
         this.world.toggled(shader.world);
         this.hideInFirstPerson.toggled(shader.hideInFirstPerson);
         this.renderOnHand.toggled(shader.renderOnHand);
         this.renderStage.setValue((double)shader.renderStage);
         this.blendMode.label = IKey.str("Режим смешивания: " + (shader.blendMode == null || shader.blendMode.isEmpty() ? "additive" : shader.blendMode));
         this.passes.setValue((double)shader.passes);
         this.scale.setValue((double)shader.scale);
         this.duration.setValue((double)shader.duration);
         this.shadowMap.toggled(shader.shadowMap);
         this.shadowResolution.setValue((double)shader.shadowResolution);
         this.shadowDistance.setValue((double)shader.shadowDistance);
         this.vertex.setText(shader.vertex);
         this.fragment.setText(shader.fragment);
         this.runtimeStatus = ClientShaderRuntime.getStatus(shader);
      } finally {
         this.filling = false;
      }
   }
}
