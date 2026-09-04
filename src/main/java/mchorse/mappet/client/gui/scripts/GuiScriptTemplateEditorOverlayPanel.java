package mchorse.mappet.client.gui.scripts;

import mchorse.mappet.client.gui.utils.overlays.GuiOverlayPanel;
import mchorse.mappet.utils.autocomplete.Config;
import mchorse.mappet.utils.autocomplete.ValueScriptTemplate;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiScriptTemplateEditorOverlayPanel extends GuiOverlayPanel {
   public final ValueScriptTemplate template;
   public final GuiTextEditor editor;
   public final GuiButtonElement save;
   public final GuiButtonElement reset;

   public GuiScriptTemplateEditorOverlayPanel(class_310 mc, ValueScriptTemplate template) {
      super(mc, IKey.lang("autocomplete.config.script_template.editor_title"));
      this.template = template;
      this.editor = new GuiTextEditor(mc, (text) -> {
      });
      this.save = new GuiButtonElement(mc, IKey.lang("autocomplete.config.script_template.save"), (button) -> this.save());
      this.reset = new GuiButtonElement(mc, IKey.lang("autocomplete.config.script_template.reset"), (button) -> this.editor.setText(Config.getDefaultNewScriptTemplate()));

      this.editor.setText(Config.decodeScriptTemplate((String)template.get()));
      this.editor.background();
      this.editor.flex().relative(this.content).xy(0, 24).w(1.0F).h(1.0F, -24);
      this.save.flex().w(80);
      this.reset.flex().w(80);

      GuiElement controls = Elements.row(mc, 5, new GuiElement[]{this.save, this.reset});
      controls.flex().relative(this.content).xy(0, 0).h(20);
      this.content.add(new IGuiElement[]{controls, this.editor});
   }

   private void save() {
      this.template.set(this.editor.getText());
      this.close();
   }
}
