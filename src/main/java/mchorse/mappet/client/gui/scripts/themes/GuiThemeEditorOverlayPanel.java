package mchorse.mappet.client.gui.scripts.themes;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import mchorse.mappet.Mappet;
import mchorse.mappet.client.gui.scripts.GuiTextEditor;
import mchorse.mappet.client.gui.scripts.utils.SyntaxStyle;
import mchorse.mappet.client.gui.utils.overlays.GuiEditorOverlayPanel;
import mchorse.mclib.client.gui.framework.elements.GuiElement;
import mchorse.mclib.client.gui.framework.elements.IGuiElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiIconElement;
import mchorse.mclib.client.gui.framework.elements.buttons.GuiToggleElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiColorElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTextElement;
import mchorse.mclib.client.gui.framework.elements.list.GuiListElement;
import mchorse.mclib.client.gui.framework.elements.modals.GuiModal;
import mchorse.mclib.client.gui.framework.elements.modals.GuiPromptModal;
import mchorse.mclib.client.gui.utils.Elements;
import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import net.minecraft.class_310;

public class GuiThemeEditorOverlayPanel extends GuiEditorOverlayPanel<GuiThemeEditorOverlayPanel.SyntaxStyleEntry> {
   public static final String CODE_SAMPLE = "/* Обычная функция */\nfunction main(c) {\n    let s = c.subject;\n}\n\n/* Ошибка в синтаксе */\nfunction error() {";
   public GuiIconElement open;
   public GuiTextElement title;
   public GuiToggleElement shadow;
   public GuiColorElement primary;
   public GuiColorElement secondary;
   public GuiColorElement identifier;
   public GuiColorElement special;
   public GuiColorElement strings;
   public GuiColorElement comments;
   public GuiColorElement numbers;
   public GuiColorElement other;
   public GuiColorElement error;
   public GuiColorElement lineNumbers;
   public GuiColorElement background;
   public GuiColorElement globalTriggerCategoryColor;
   public GuiTextEditor preview;

   public GuiThemeEditorOverlayPanel(class_310 mc) {
      super(mc, IKey.lang("mappet.gui.syntax_theme.main"));
      this.open = new GuiIconElement(mc, Icons.FOLDER, (b) -> Themes.open());
      this.open.tooltip(IKey.lang("mappet.gui.syntax_theme.folder")).flex().wh(16, 16);
      this.title = new GuiTextElement(mc, 100, (s) -> (this.item).style.title = s);
      this.shadow = new GuiToggleElement(mc, IKey.lang("mappet.gui.syntax_theme.shadow"), (b) -> (this.item).style.shadow = b.isToggled());
      this.primary = new GuiColorElement(mc, (c) -> {
         (this.item).style.primary = c;
         this.preview.resetHighlight();
      });
      this.primary.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.primary"));
      this.secondary = new GuiColorElement(mc, (c) -> {
         (this.item).style.secondary = c;
         this.preview.resetHighlight();
      });
      this.secondary.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.secondary"));
      this.identifier = new GuiColorElement(mc, (c) -> {
         (this.item).style.identifier = c;
         this.preview.resetHighlight();
      });
      this.identifier.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.identifier"));
      this.special = new GuiColorElement(mc, (c) -> {
         (this.item).style.special = c;
         this.preview.resetHighlight();
      });
      this.special.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.special"));
      this.strings = new GuiColorElement(mc, (c) -> {
         (this.item).style.strings = c;
         this.preview.resetHighlight();
      });
      this.strings.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.strings"));
      this.comments = new GuiColorElement(mc, (c) -> {
         (this.item).style.comments = c;
         this.preview.resetHighlight();
      });
      this.comments.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.comments"));
      this.numbers = new GuiColorElement(mc, (c) -> {
         (this.item).style.numbers = c;
         this.preview.resetHighlight();
      });
      this.numbers.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.numbers"));
      this.other = new GuiColorElement(mc, (c) -> {
         (this.item).style.other = c;
         this.preview.resetHighlight();
      });
      this.other.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.other"));
      this.error = new GuiColorElement(mc, (c) -> {
         (this.item).style.error = c;
         this.preview.resetHighlight();
      });
      this.error.tooltip(IKey.lang("mappet.gui.syntax_theme.colors.error"));
      this.lineNumbers = new GuiColorElement(mc, (c) -> (this.item).style.lineNumbers = c);
      this.lineNumbers.tooltip(IKey.lang("mappet.gui.syntax_theme.background_colors.line_numbers"));
      this.background = new GuiColorElement(mc, (c) -> (this.item).style.background = c);
      this.background.tooltip(IKey.lang("mappet.gui.syntax_theme.background_colors.background"));
      this.globalTriggerCategoryColor = new GuiColorElement(mc, Mappet.globalTriggerCategoryColor);
      this.globalTriggerCategoryColor.tooltip(IKey.str("Цвет подсветки выбранной категории в списке глобальных триггеров"));
      this.preview = new GuiTextEditor(mc, (Consumer)null);
      this.editor.add(new IGuiElement[]{Elements.label(IKey.lang("mappet.gui.syntax_theme.title")), this.title, this.shadow});
      this.editor.add(Elements.label(IKey.lang("mappet.gui.syntax_theme.colors.title")).marginTop(12));
      this.editor.add(Elements.row(mc, 5, new GuiElement[]{this.primary, this.secondary}));
      this.editor.add(Elements.row(mc, 5, new GuiElement[]{this.identifier, this.special}));
      this.editor.add(Elements.row(mc, 5, new GuiElement[]{this.strings, this.comments}));
      this.editor.add(Elements.row(mc, 5, new GuiElement[]{this.numbers, this.other}));
      this.editor.add(Elements.row(mc, 5, new GuiElement[]{this.error}));
      this.editor.add(Elements.label(IKey.lang("mappet.gui.syntax_theme.background_colors.title")).marginTop(12));
      this.editor.add(Elements.row(mc, 5, new GuiElement[]{this.lineNumbers, this.background}));
      this.editor.add(Elements.label(IKey.str("Глобальные триггеры")).marginTop(12));
      this.editor.add(this.globalTriggerCategoryColor);
      this.content.flex().h(0.5F);
      this.preview.flex().relative(this).y(0.5F, 28).w(1.0F).hTo(this.area, 1.0F);
      this.preview.setText(CODE_SAMPLE);
      this.add(this.preview.background());
      this.icons.add(this.open);
      this.loadThemes();
   }

   private void loadThemes() {
      for(File file : Themes.themes()) {
         SyntaxStyle style = Themes.readTheme(file);
         if (style != null) {
            SyntaxStyleEntry entry = new SyntaxStyleEntry(file, style);
            this.list.add(entry);
         }
      }

      if (this.list.getList().isEmpty()) {
         SyntaxStyle thema = new SyntaxStyle();

         this.list.add(new SyntaxStyleEntry(Themes.themeFile("themakoroche.json"), thema));
      }

      for(SyntaxStyleEntry entry : this.list.getList()) {
         if (entry.file.getName().equals(Mappet.scriptEditorSyntaxStyle.getFile())) {
            this.pickItem(entry, true);
            break;
         }
      }

      if (this.list.isDeselected()) {
         this.list.setIndex(0);
         this.pickItem((SyntaxStyleEntry)this.list.getCurrentFirst(), true);
      }
   }

   protected GuiListElement<SyntaxStyleEntry> createList(class_310 mc) {
      return new GuiSyntaxStyleListElement(mc, (l) -> this.pickItem((SyntaxStyleEntry)l.get(0), false));
   }

   protected IKey getAddLabel() {
      return IKey.lang("mappet.gui.syntax_theme.context.add");
   }

   protected IKey getRemoveLabel() {
      return IKey.lang("mappet.gui.syntax_theme.context.remove");
   }

   protected void addItem() {
      GuiModal.addFullModal(this, () -> new GuiPromptModal(this.mc, IKey.lang("mappet.gui.syntax_theme.modal.add"), this::addNewTheme));
   }

   private void addNewTheme(String string) {
      File file = Themes.themeFile(string);
      if (!file.isFile()) {
         SyntaxStyle style = new SyntaxStyle();
         SyntaxStyleEntry entry = new SyntaxStyleEntry(file, style);
         style.title = "";
         this.list.add(entry);
         this.list.update();
         this.pickItem(entry, true);
      }

   }

   protected void removeItem() {
      ((SyntaxStyleEntry)this.list.getCurrentFirst()).file.delete();
      super.removeItem();
   }

   protected void pickItem(SyntaxStyleEntry item, boolean select) {
      item.save();
      Mappet.scriptEditorSyntaxStyle.set(item.file.getName(), item.style);
      this.preview.getHighlighter().setStyle(item.style);
      this.preview.resetHighlight();
      super.pickItem(item, select);
   }

   protected void fillData(SyntaxStyleEntry item) {
      this.title.setText(item.style.title);
      this.shadow.toggled(item.style.shadow);
      this.primary.picker.setColor(item.style.primary);
      this.secondary.picker.setColor(item.style.secondary);
      this.identifier.picker.setColor(item.style.identifier);
      this.special.picker.setColor(item.style.special);
      this.strings.picker.setColor(item.style.strings);
      this.comments.picker.setColor(item.style.comments);
      this.numbers.picker.setColor(item.style.numbers);
      this.error.picker.setColor(item.style.error);
      this.lineNumbers.picker.setColor(item.style.lineNumbers);
      this.background.picker.setColor(item.style.background);
      this.other.picker.setColor(item.style.other);
   }

   public void onClose() {
      SyntaxStyleEntry item = (SyntaxStyleEntry)this.list.getCurrentFirst();
      item.save();
      Mappet.scriptEditorSyntaxStyle.set(item.file.getName(), item.style);
      super.onClose();
   }

   public static class GuiSyntaxStyleListElement extends GuiListElement<SyntaxStyleEntry> {
      public GuiSyntaxStyleListElement(class_310 mc, Consumer<List<SyntaxStyleEntry>> callback) {
         super(mc, callback);
         this.scroll.scrollItemSize = 16;
      }

      protected String elementToString(SyntaxStyleEntry element) {
         if (element.style.title.trim().isEmpty()) {
            return element.file.getName();
         } else {
            String var10000 = element.style.title;
            return var10000 + " (" + element.file.getName() + ")";
         }
      }
   }

   public static class SyntaxStyleEntry {
      public File file;
      public SyntaxStyle style;

      public SyntaxStyleEntry(File file, SyntaxStyle style) {
         this.file = file;
         this.style = style;
      }

      public void save() {
         Themes.writeTheme(this.file, this.style);
      }
   }
}
